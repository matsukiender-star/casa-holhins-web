package mx.holhins.dao;

import mx.holhins.modelo.Usuario;
import mx.holhins.util.PasswordUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {

    @BeforeAll
    public static void setup() {
        ConexionDB.setTestMode(true);
    }

    @Test
    public void testBuscarPorUsername() {
        UsuarioDAO dao = new UsuarioDAO();
        // admin es insertado por data.sql
        Usuario u = dao.buscarPorUsername("admin");
        
        assertNotNull(u);
        assertEquals("admin", u.getUsername());
        assertEquals("ADMIN", u.getRol());
        
        // Verificar contrasena de prueba "admin123" (configurada en data.sql)
        assertTrue(PasswordUtil.verifyPassword("admin123", u.getPasswordHash()));
    }
    
    @Test
    public void testBuscarUsuarioInexistente() {
        UsuarioDAO dao = new UsuarioDAO();
        Usuario u = dao.buscarPorUsername("noexiste");
        assertNull(u);
    }
}
