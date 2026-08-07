package mx.holhins.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fabrica de conexiones a la base H2. Todo el acceso a datos del sistema pasa
 * por aqui, asi que si algun dia migramos a MySQL o Postgres solo se toca esta
 * clase y lo demas ni se entera.
 *
 * Manejamos dos bases distintas:
 *   - la de archivo (./data/holhins) para cuando el sistema corre de verdad
 *   - una en memoria para los tests, que se muere al terminar la JVM
 *
 * OJO con la inicializacion: antes los scripts se lanzaban con el parametro
 * INIT=RUNSCRIPT dentro de la URL, y eso resultaba ser una trampa. H2 corre ese
 * INIT en CADA conexion que se abre, no una sola vez, asi que data.sql se
 * re-ejecutaba constantemente y regresaba los servicios a sus valores
 * originales: si el usuario desactivaba o editaba un servicio, el cambio se
 * deshacia solo en la siguiente peticion y sin avisar. Ahora sembramos la base
 * una unica vez (ver inicializarSiHaceFalta) y la URL queda limpia.
 */
public class ConexionDB {

    private static final Logger LOG = Logger.getLogger(ConexionDB.class.getName());

    // Base de archivo: los datos sobreviven a reinicios de Tomcat.
    private static final String URL_ARCHIVO = "jdbc:h2:./data/holhins";

    // Base en memoria para tests. DB_CLOSE_DELAY=-1 la mantiene viva mientras
    // la JVM siga arriba, si no se borraria al cerrar la primera conexion.
    private static final String URL_TEST = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static boolean modoTest = false;

    // Bandera para sembrar la base una sola vez por arranque.
    private static boolean inicializada = false;

    /**
     * Cambia entre la base real y la de pruebas. Lo llaman los tests en su
     * @BeforeAll. Al cambiar de base reseteamos la bandera, porque la nueva
     * todavia no tiene ni tablas ni datos.
     */
    public static synchronized void setTestMode(boolean testMode) {
        if (modoTest != testMode) {
            modoTest = testMode;
            inicializada = false;
        }
    }

    /**
     * Devuelve una conexion lista para usar. Quien la pida es responsable de
     * cerrarla, y para eso todos los DAOs usan try-with-resources (el
     * equivalente al 'with' de Python: al salir del bloque se cierra sola).
     */
    public static Connection getConnection() throws SQLException {
        inicializarSiHaceFalta();
        return abrirConexion();
    }

    /** Conexion cruda, sin pasar por la inicializacion (la usa el sembrado). */
    private static Connection abrirConexion() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            // Si falta el driver no hay nada que hacer: mejor tronar aqui con un
            // mensaje claro que dejar que falle raro tres capas mas arriba.
            throw new SQLException("No se encontro el driver de H2 en el classpath", e);
        }
        return DriverManager.getConnection(modoTest ? URL_TEST : URL_ARCHIVO, USER, PASSWORD);
    }

    /**
     * Crea las tablas y siembra los datos iniciales, una sola vez por arranque.
     *
     * schema.sql se puede correr siempre sin miedo porque usa
     * CREATE TABLE IF NOT EXISTS. data.sql en cambio solo se corre si la base
     * esta vacia: asi los cambios que haga el usuario desde la aplicacion no se
     * pisan cada vez que Tomcat reinicia.
     *
     * Va synchronized porque Tomcat atiende peticiones en paralelo y no queremos
     * dos hilos sembrando la base al mismo tiempo.
     */
    private static synchronized void inicializarSiHaceFalta() throws SQLException {
        if (inicializada) {
            return;
        }
        try (Connection con = abrirConexion(); Statement st = con.createStatement()) {
            st.execute("RUNSCRIPT FROM 'classpath:schema.sql'");

            if (baseSinDatos(st)) {
                st.execute("RUNSCRIPT FROM 'classpath:data.sql'");
                LOG.info("Base de datos sembrada con los datos iniciales de data.sql");
            }

            migrar(st);
            inicializada = true;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Fallo al inicializar la base de datos", e);
            throw e;
        }
    }

    /**
     * Pone al dia una base que ya existia desde antes.
     *
     * Aqui esta la leccion mas cara del proyecto. schema.sql usa
     * CREATE TABLE IF NOT EXISTS, que es justo lo que queremos para no borrar
     * datos al reiniciar... pero tiene un costo que no habiamos visto: si la
     * tabla ya existe, H2 se salta la instruccion COMPLETA. No compara nada, no
     * actualiza nada. O sea que cualquier correccion posterior al esquema jamas
     * llega a una base que ya estaba creada: solo la ven las bases nuevas.
     *
     * Eso mantuvo dos bugs vivos durante semanas sin que los tests dijeran ni
     * pio, porque los tests corren contra una base en memoria que nace limpia en
     * cada ejecucion y por definicion siempre tiene el esquema al dia.
     *
     * Por eso las correcciones de esquema van aqui y no en schema.sql: este
     * metodo si mira como esta la base de verdad y la ajusta. Es idempotente,
     * puede correr en cada arranque sin hacer dano.
     */
    static void migrar(Statement st) throws SQLException { // visible para los tests de migracion
        alinearCheckDeEstatus(st);
        resincronizarIdentidades(st);
    }

    /**
     * Se asegura de que el CHECK de clientes.estatus admita 'INACTIVO'.
     *
     * Sintoma que arreglaba: dar de baja un cliente no hacia nada. bajaLogica()
     * escribe 'INACTIVO', pero las bases creadas antes de esa correccion traian
     * el CHECK viejo con solo ('ACTIVO','LAPSADO','NUEVO'), asi que el UPDATE
     * moria por violacion de constraint. Como el DAO se tragaba la excepcion, la
     * pantalla se recargaba con el cliente intacto y sin ningun mensaje.
     */
    private static void alinearCheckDeEstatus(Statement st) throws SQLException {
        List<String> obsoletos = new ArrayList<>();

        // Buscamos los CHECK de la tabla clientes cuya definicion no mencione
        // INACTIVO. El nombre lo genera H2 solo (CONSTRAINT_64 y demas), por eso
        // hay que preguntarlo en vez de asumirlo.
        String sql =
            "SELECT tc.CONSTRAINT_NAME "
          + "FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc "
          + "JOIN INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc "
          + "  ON tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME "
          + "WHERE tc.TABLE_NAME = 'CLIENTES' "
          + "  AND tc.CONSTRAINT_TYPE = 'CHECK' "
          + "  AND UPPER(cc.CHECK_CLAUSE) NOT LIKE '%INACTIVO%'";

        try (ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                obsoletos.add(rs.getString(1));
            }
        }

        if (obsoletos.isEmpty()) {
            return; // la base ya esta al dia, no hay nada que hacer
        }

        for (String nombre : obsoletos) {
            st.execute("ALTER TABLE clientes DROP CONSTRAINT \"" + nombre + "\"");
        }
        st.execute("ALTER TABLE clientes ADD CONSTRAINT ck_clientes_estatus "
                 + "CHECK (estatus IN ('ACTIVO','LAPSADO','NUEVO','INACTIVO'))");

        LOG.info("Migracion: se actualizo el CHECK de clientes.estatus para admitir INACTIVO");
    }

    /**
     * Realinea los contadores de auto-incremento con los ids que ya existen.
     *
     * Sintoma que arreglaba: guardar un servicio nuevo no guardaba nada. Los
     * servicios de data.sql se siembran con id explicito, y H2 (con GENERATED BY
     * DEFAULT AS IDENTITY) no adelanta el contador cuando le das el id tu mismo.
     * El contador se quedaba en 3 mientras el MAX(id) real era 5, asi que el
     * siguiente alta pedia un id ya ocupado y reventaba por clave primaria
     * duplicada. Y otra vez: excepcion tragada, redirect, cero avisos.
     *
     * Es tambien la razon de que desactivar un servicio si funcionara y crearlo
     * no: desactivar es un UPDATE y ni se acerca al contador.
     */
    private static void resincronizarIdentidades(Statement st) throws SQLException {
        for (String tabla : new String[] { "usuarios", "clientes", "servicios" }) {
            int siguiente;
            try (ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM " + tabla)) {
                siguiente = rs.next() ? rs.getInt(1) : 1;
            }
            // RESTART WITH no acepta subconsulta, por eso calculamos el numero
            // antes y lo pegamos ya resuelto.
            st.execute("ALTER TABLE " + tabla + " ALTER COLUMN id RESTART WITH " + siguiente);
        }
    }

    /** True si todavia no hay usuarios, o sea que la base esta recien creada. */
    private static boolean baseSinDatos(Statement st) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}
