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

/**
 * Pantalla de inicio una vez que entras al sistema. Junta los numeros que la
 * Directora quiere ver de un vistazo: cuantos clientes activos hay, cuantos
 * servicios se estan ofreciendo y quienes son los ultimos clientes registrados.
 *
 * Es el servlet mas simple del proyecto justamente porque no calcula nada: le
 * pide los datos ya masticados a los DAOs y los deja en el request para que la
 * JSP los pinte. Si algun dia se agregan KPIs de verdad (ver el roadmap del
 * README), la logica de esos calculos va en la capa de datos, no aqui.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();

    /** Cuantos clientes recientes se muestran en la tarjeta de abajo. */
    private static final int RECIENTES = 5;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("totalClientes", clienteDAO.contarActivos());
        req.setAttribute("totalServicios", servicioDAO.contarActivos());

        // listarPaginado ya viene ordenado por id DESC, asi que los primeros 5
        // son los ultimos que se dieron de alta.
        List<Cliente> ultimosClientes = clienteDAO.listarPaginado(0, RECIENTES);
        req.setAttribute("ultimosClientes", ultimosClientes);

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
