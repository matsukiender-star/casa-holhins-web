package mx.holhins.dao;

import mx.holhins.modelo.Servicio;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServicioDAOTest {
    private static ServicioDAO servicioDAO;
    
    @BeforeAll
    static void setup() {
        ConexionDB.setTestMode(true);
        servicioDAO = new ServicioDAO();
    }
    
    @Test
    @Order(1)
    @DisplayName("Actualizar servicio persiste cambios entre conexiones")
    void actualizarServicio_persisteCambiosEntreConexiones() throws SQLException {
        Servicio s = new Servicio();
        s.setNombre("Test Servicio Original");
        s.setDescripcion("Descripción original");
        s.setTipo("TERAPIA");
        s.setDuracionMinutos(60);
        s.setPrecio(new BigDecimal("300.00"));
        s.setActivo(true);
        
        servicioDAO.insertar(s);
        
        List<Servicio> servicios = servicioDAO.listarPorTipo("TERAPIA");
        Servicio insertado = servicios.stream().filter(serv -> "Test Servicio Original".equals(serv.getNombre())).findFirst().orElse(null);
        assertNotNull(insertado, "El servicio debe crearse");
        int idOriginal = insertado.getId();
        
        insertado.setNombre("Test Servicio MODIFICADO");
        insertado.setPrecio(new BigDecimal("999.99"));
        servicioDAO.actualizar(insertado);
        
        Servicio recargado = servicioDAO.buscarPorId(idOriginal);
        assertNotNull(recargado);
        assertEquals("Test Servicio MODIFICADO", recargado.getNombre(), "El nombre debe haberse persistido");
        assertEquals(new BigDecimal("999.99"), recargado.getPrecio(), "El precio debe haberse persistido");
    }
    
    @Test
    @Order(2)
    @DisplayName("Desactivar servicio persiste entre conexiones")
    void desactivarServicio_persisteEntreConexiones() throws SQLException {
        Servicio s = new Servicio();
        s.setNombre("Test Desactivación");
        s.setTipo("TALLER");
        s.setDuracionMinutos(120);
        s.setPrecio(new BigDecimal("500.00"));
        s.setActivo(true);
        
        servicioDAO.insertar(s);
        
        List<Servicio> servicios = servicioDAO.listarPorTipo("TALLER");
        Servicio insertado = servicios.stream().filter(serv -> "Test Desactivación".equals(serv.getNombre())).findFirst().orElse(null);
        assertNotNull(insertado, "El servicio debe crearse");
        int id = insertado.getId();
        
        servicioDAO.desactivar(id);
        
        Servicio recargado = servicioDAO.buscarPorId(id);
        assertFalse(recargado.getActivo(), "El servicio debe seguir desactivado tras releer");
    }
}
