package mx.holhins.servlet;

import mx.holhins.dao.UsuarioDAO;
import mx.holhins.modelo.Usuario;
import mx.holhins.util.CsrfUtil;
import mx.holhins.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Puerta de entrada al sistema. El GET pinta el formulario y el POST valida las
 * credenciales.
 *
 * El servlet no sabe nada de SQL ni de como se guardan las contrasenas: le pide
 * el usuario al DAO y la comparacion del hash a PasswordUtil. Su unico trabajo
 * es coordinar y decidir a donde mandar al visitante, que es justo lo que le
 * toca al controlador en MVC.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Creamos la sesion desde ya (aunque nadie haya entrado todavia) para
        // poder colgar de ella el token CSRF que va escondido en el formulario.
        CsrfUtil.obtenerToken(req.getSession());
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Primero el token: si no cuadra, ni nos molestamos en mirar el usuario.
        if (!CsrfUtil.esValido(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad invalido");
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Usuario user = usuarioDAO.buscarPorUsername(username);

        // Ojo con el orden del OR corto: si el usuario no existe, user es null y
        // ni siquiera intentamos verificar el hash.
        if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {

            // Cambiamos el id de sesion al autenticar. Esto corta el ataque de
            // fijacion de sesion: si alguien te habia plantado un id conocido
            // antes de entrar, ese id deja de servirle en cuanto inicias sesion.
            req.changeSessionId();

            HttpSession session = req.getSession();
            session.setAttribute("usuario", user);
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } else {
            // Mensaje generico a proposito: no decimos "el usuario no existe" ni
            // "la contrasena esta mal", porque eso le confirmaria a un atacante
            // que nombres de usuario son validos.
            req.setAttribute("error", "Credenciales incorrectas. Intenta de nuevo.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
