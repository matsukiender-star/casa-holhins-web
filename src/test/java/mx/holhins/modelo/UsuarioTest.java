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

    /**
     * El saludo de pantalla usa nombre de pila mas apellido, no el nombre
     * completo: lo pidio la Directora porque los cuatro nombres se sienten
     * acartonados y en la casa se trata de corto.
     */
    @Test
    public void nombreCorto_devuelvePrimerNombreYApellido() {
        Usuario u = new Usuario();
        u.setNombreCompleto("Ana María Trejo Holhins");
        assertEquals("Ana Holhins", u.getNombreCorto());
    }

    @Test
    public void nombreCorto_conUnaSolaPalabra_laDevuelveTalCual() {
        Usuario u = new Usuario();
        u.setNombreCompleto("Secretaria");
        assertEquals("Secretaria", u.getNombreCorto());
    }

    @Test
    public void nombreCorto_sinNombre_noRevienta() {
        assertNull(new Usuario().getNombreCorto());
    }
}
