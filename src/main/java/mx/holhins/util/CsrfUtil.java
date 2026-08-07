package mx.holhins.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Proteccion contra CSRF (Cross-Site Request Forgery).
 *
 * El ataque va asi: tu entras al sistema y dejas la sesion abierta. Luego, sin
 * cerrarla, abres una pagina cualquiera que trae escondido un formulario que
 * apunta a nuestro /clientes. El navegador manda la cookie de sesion sin
 * preguntarte, y para el servidor ese POST parece legitimo. Resultado: la otra
 * pagina acaba dando de alta o modificando registros en tu nombre.
 *
 * La defensa es un token secreto que solo vive en la sesion y viaja en cada
 * formulario como campo oculto. Una pagina externa no puede leerlo (se lo
 * impide la politica de mismo origen del navegador), asi que su POST llega sin
 * token y lo rechazamos.
 *
 * Es el mismo mecanismo que el csrf_token de Django o el CSRFProtect de Flask,
 * nada mas que aqui lo armamos a mano porque Servlets puros no lo traen.
 */
public class CsrfUtil {

    /** Nombre del atributo en sesion y del input oculto en los formularios. */
    public static final String TOKEN = "csrfToken";

    // SecureRandom y no Random: el segundo es predecible si adivinas la semilla,
    // y un token adivinable no sirve de nada.
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Devuelve el token de la sesion y lo crea si es la primera vez.
     * El token dura toda la sesion; al hacer logout se invalida con ella.
     */
    public static String obtenerToken(HttpSession session) {
        String token = (String) session.getAttribute(TOKEN);
        if (token == null) {
            byte[] bytes = new byte[32]; // 256 bits, de sobra
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(TOKEN, token);
        }
        return token;
    }

    /**
     * Compara el token que llego en el formulario contra el de la sesion.
     * Si no hay sesion, o no hay token, o no coinciden, la peticion no pasa.
     */
    public static boolean esValido(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String enSesion = (String) session.getAttribute(TOKEN);
        String enFormulario = req.getParameter(TOKEN);

        return enSesion != null
            && enFormulario != null
            && constantes(enSesion, enFormulario);
    }

    /**
     * Compara dos cadenas sin cortocircuito, siempre en el mismo tiempo.
     *
     * Un equals() normal regresa false en cuanto encuentra el primer caracter
     * distinto, y midiendo cuanto tarda se puede ir adivinando el token letra
     * por letra (ataque de temporizacion). Recorriendo siempre todo se evita esa
     * filtracion.
     */
    private static boolean constantes(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diferencia = 0;
        for (int i = 0; i < a.length(); i++) {
            diferencia |= a.charAt(i) ^ b.charAt(i);
        }
        return diferencia == 0;
    }
}
