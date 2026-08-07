package mx.holhins.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            inicializada = true;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Fallo al inicializar la base de datos", e);
            throw e;
        }
    }

    /** True si todavia no hay usuarios, o sea que la base esta recien creada. */
    private static boolean baseSinDatos(Statement st) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}
