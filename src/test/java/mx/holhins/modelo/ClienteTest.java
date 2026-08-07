package mx.holhins.modelo;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    public void testCreacionCliente() {
        Cliente c = new Cliente();
        c.setNombreCompleto("Juan Perez");
        c.setTelefono("5512345678");
        c.setEstatus("NUEVO");
        
        assertEquals("Juan Perez", c.getNombreCompleto());
        assertEquals("NUEVO", c.getEstatus());
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        c.setFechaUltimaVisita(now);
        assertEquals(now, c.getFechaUltimaVisita());
    }
}
