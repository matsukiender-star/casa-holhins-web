package mx.holhins.dao;

import mx.holhins.modelo.Cliente;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ClienteDAOTest {

    @BeforeAll
    public static void setup() {
        ConexionDB.setTestMode(true);
    }

    @Test
    public void testAltaYConsulta() {
        ClienteDAO dao = new ClienteDAO();
        
        Cliente c = new Cliente();
        c.setNombreCompleto("Prueba JUnit");
        c.setTelefono("5555555555");
        c.setEstatus("NUEVO");
        
        int countAntes = dao.contarActivos();
        dao.insertar(c);
        int countDespues = dao.contarActivos();
        
        assertEquals(countAntes + 1, countDespues);
        
        List<Cliente> clientes = dao.listarPaginado(0, 10);
        assertFalse(clientes.isEmpty());
        
        Cliente ultimo = clientes.get(0); // orden DESC
        assertEquals("Prueba JUnit", ultimo.getNombreCompleto());
        
        // Test update estatus
        ultimo.setEstatus("ACTIVO");
        dao.actualizar(ultimo);
        
        Cliente actualizado = dao.buscarPorId(ultimo.getId());
        assertEquals("ACTIVO", actualizado.getEstatus());
    }

    @Test
    public void bajaLogica_marcaClienteComoInactivo_yYaNoApareceEnListado() {
        ClienteDAO dao = new ClienteDAO();
        Cliente c = new Cliente();
        c.setNombreCompleto("Cliente Test Baja");
        c.setTelefono("5551234567");
        c.setCorreo("test@baja.mx");
        c.setEstatus("ACTIVO");
        
        int cantidadAntes = dao.contarActivos();
        dao.insertar(c);
        
        Cliente insertado = dao.listarPaginado(0, 1).get(0);
        
        dao.bajaLogica(insertado.getId());
        
        Cliente actualizado = dao.buscarPorId(insertado.getId());
        assertNotNull(actualizado, "El cliente sigue existiendo en BD");
        assertEquals("INACTIVO", actualizado.getEstatus(), "El estatus debe ser INACTIVO tras la baja");
        assertEquals(cantidadAntes, dao.contarActivos(), "Debe haber la misma cantidad que antes de insertar");
    }
}
