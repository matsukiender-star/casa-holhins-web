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
}
