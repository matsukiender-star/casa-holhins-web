package mx.holhins.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Manejo de contrasenas con bcrypt.
 *
 * En la base NUNCA guardamos la contrasena en claro, solo el hash. Si alguien
 * se robara la tabla usuarios veria puros $2a$12$... y de ahi no se saca la
 * contrasena original. En Python haciamos lo mismo con
 * bcrypt.hashpw(pwd.encode(), bcrypt.gensalt()).
 *
 * Por que bcrypt y no SHA-256: los hash normales estan hechos para ser rapidos,
 * y eso juega en contra aqui, porque una tarjeta grafica puede probar millones
 * de combinaciones por segundo. bcrypt es lento a proposito y ademas ajustable:
 * el "cost" dice cuantas rondas hace, y cada punto duplica el trabajo. Con
 * cost 12 son 4096 rondas, unos cientos de milisegundos por verificacion: ni se
 * nota al entrar, pero vuelve carisimo un ataque de fuerza bruta.
 *
 * El salt no hay que manejarlo a mano: bcrypt genera uno aleatorio por
 * contrasena y lo guarda dentro del propio hash. Por eso dos usuarios con la
 * misma contrasena terminan con hashes distintos.
 */
public class PasswordUtil {

    /** Rondas de bcrypt. Subirlo hace el login mas lento y mas resistente. */
    private static final int COST = 12;

    /** Devuelve el hash listo para guardar en la columna password_hash. */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(COST, password.toCharArray());
    }

    /**
     * Compara una contrasena en claro contra el hash guardado.
     *
     * No se hashea de nuevo para comparar cadenas: bcrypt saca el salt y el cost
     * del propio hash y hace la comparacion en tiempo constante, sin filtrar
     * informacion por lo que tarda.
     */
    public static boolean verifyPassword(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return result.verified;
    }
}
