package mx.holhins.servlet;

import mx.holhins.dao.UsuarioDAO;
import mx.holhins.modelo.Usuario;
import mx.holhins.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        
        Usuario user = usuarioDAO.buscarPorUsername(username);
        
        // Verificamos si el usuario existe y si la password es correcta usando BCrypt
        if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            HttpSession session = req.getSession();
            session.setAttribute("usuario", user);
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.setAttribute("error", "Credenciales incorrectas. Intenta de nuevo.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
