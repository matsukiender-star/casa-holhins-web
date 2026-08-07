package mx.holhins.servlet;

import mx.holhins.dao.ClienteDAO;
import mx.holhins.dao.ServicioDAO;
import mx.holhins.modelo.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private ServicioDAO servicioDAO = new ServicioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // Contadores
        int totalClientes = clienteDAO.contarActivos();
        int totalServicios = servicioDAO.contarActivos();
        
        // Ultimos 5 clientes
        List<Cliente> ultimosClientes = clienteDAO.listarPaginado(0, 5);
        
        req.setAttribute("totalClientes", totalClientes);
        req.setAttribute("totalServicios", totalServicios);
        req.setAttribute("ultimosClientes", ultimosClientes);
        
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
