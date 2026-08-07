package mx.holhins.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    public void testHashAndVerify() {
        String pass = "secreta123";
        String hash = PasswordUtil.hashPassword(pass);
        
        assertNotNull(hash);
        assertNotEquals(pass, hash);
        assertTrue(hash.startsWith("$2a$12$"));
        
        assertTrue(PasswordUtil.verifyPassword(pass, hash));
        assertFalse(PasswordUtil.verifyPassword("incorrecta", hash));
    }
}
