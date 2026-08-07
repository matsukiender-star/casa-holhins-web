package mx.holhins.dao;

import mx.holhins.modelo.Cliente;
import mx.holhins.modelo.Servicio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regresion de los dos bugs que aparecieron al probar el sistema para el video.
 *
 * Los dos venian del mismo sitio: schema.sql usa CREATE TABLE IF NOT EXISTS, asi
 * que una base creada hace tiempo nunca recibe las correcciones posteriores del
 * esquema. Solo las bases nuevas las ven.
 *
 * Y por eso ningun test los detecto en su momento: los tests corren contra una
 * base en memoria que nace limpia en cada ejecucion, o sea el unico escenario en
 * el que el bug no puede darse. Aqui hacemos lo contrario: ensuciamos la base a
 * proposito para dejarla como estaba la de produccion, y comprobamos que
 * ConexionDB.migrar() la repara.
 */
public class MigracionTest {

    @BeforeAll
    public static void setup() {
        ConexionDB.setTestMode(true);
    }

    /**
     * Bug: "no me deja eliminar el cliente que registre".
     *
     * Las bases viejas traen el CHECK de estatus sin 'INACTIVO', que es justo el
     * valor que escribe bajaLogica(). El UPDATE moria por violacion de
     * constraint y el cliente se quedaba en la lista.
     */
    @Test
    @DisplayName("La migracion repara el CHECK de estatus para que la baja logica funcione")
    public void migracion_reparaCheckDeEstatus_yPermiteBajaLogica() throws SQLException {
        ClienteDAO dao = new ClienteDAO();

        Cliente c = new Cliente();
        c.setNombreCompleto("Cliente Base Vieja");
        c.setTelefono("5550000000");
        c.setEstatus("ACTIVO");
        dao.insertar(c);
        int id = dao.listarPaginado(0, 1).get(0).getId();

        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            degradarCheckDeEstatus(st);

            // Con el CHECK viejo la baja tiene que fallar, y fallar de forma
            // VISIBLE: antes se tragaba la excepcion y no pasaba nada.
            assertThrows(DatosException.class, () -> dao.bajaLogica(id),
                    "Con el CHECK viejo la baja debe reventar, no fallar en silencio");

            ConexionDB.migrar(st);
        }

        dao.bajaLogica(id);
        assertEquals("INACTIVO", dao.buscarPorId(id).getEstatus(),
                "Tras migrar, la baja logica debe poder escribir INACTIVO");
    }

    /**
     * Bug: "el servicio nuevo no se guarda, pero si me deja eliminar servicios".
     *
     * data.sql siembra los servicios con id explicito y H2 no adelanta el
     * contador de identidad cuando le das el id tu mismo. El contador se quedaba
     * atras del MAX(id) real, asi que el siguiente alta pedia un id ya ocupado y
     * reventaba por clave duplicada. Desactivar seguia funcionando porque es un
     * UPDATE y no toca el contador.
     */
    @Test
    @DisplayName("La migracion realinea el contador de ids para que se puedan crear servicios")
    public void migracion_realineaContadorDeIds_yPermiteInsertar() throws SQLException {
        ServicioDAO dao = new ServicioDAO();

        try (Connection con = ConexionDB.getConnection(); Statement st = con.createStatement()) {
            // Dejamos el contador atrasado a proposito, como estaba en la base real.
            st.execute("ALTER TABLE servicios ALTER COLUMN id RESTART WITH 1");

            assertThrows(DatosException.class, () -> dao.insertar(nuevoServicio("Choca Por Id")),
                    "Con el contador atrasado el alta debe reventar, no fallar en silencio");

            ConexionDB.migrar(st);
        }

        int antes = dao.listarPorTipo(null).size();
        dao.insertar(nuevoServicio("Servicio Tras Migracion"));
        assertEquals(antes + 1, dao.listarPorTipo(null).size(),
                "Tras migrar, un servicio nuevo si debe quedar guardado");
    }

    /** Deja el CHECK de estatus como lo tenian las bases anteriores a la correccion. */
    private void degradarCheckDeEstatus(Statement st) throws SQLException {
        st.execute("ALTER TABLE clientes DROP CONSTRAINT IF EXISTS ck_clientes_estatus");
        // NOCHECK para que no valide las filas que ya existen. Es exactamente el
        // estado en el que estaba la base real: el constraint viejo convivia con
        // datos que no lo cumplian.
        st.execute("ALTER TABLE clientes ADD CONSTRAINT ck_clientes_estatus "
                 + "CHECK (estatus IN ('ACTIVO','LAPSADO','NUEVO')) NOCHECK");
    }

    private Servicio nuevoServicio(String nombre) {
        Servicio s = new Servicio();
        s.setNombre(nombre);
        s.setTipo("TALLER");
        s.setPrecio(new BigDecimal("100.00"));
        s.setDuracionMinutos(60);
        s.setActivo(true);
        return s;
    }
}
