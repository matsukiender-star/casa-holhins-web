package mx.holhins.modelo;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    public void testCreacionUsuario() {
        Usuario u = new Usuario();
        u.setUsername("testuser");
        u.setNombreCompleto("Test User");
        u.setRol("ADMIN");
        u.setActivo(true);
        
        assertEquals("testuser", u.getUsername());
        assertEquals("Test User", u.getNombreCompleto());
        assertEquals("ADMIN", u.getRol());
        assertTrue(u.getActivo());
    }
}
