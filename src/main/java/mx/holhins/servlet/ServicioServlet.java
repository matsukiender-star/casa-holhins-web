package mx.holhins.servlet;

import mx.holhins.dao.ServicioDAO;
import mx.holhins.modelo.Servicio;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/servicios")
public class ServicioServlet extends HttpServlet {

    private ServicioDAO servicioDAO = new ServicioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String nuevoParam = req.getParameter("nuevo");
        String editarParam = req.getParameter("editar");
        String tipoParam = req.getParameter("tipo");

        if (nuevoParam != null) {
            req.getRequestDispatcher("/WEB-INF/views/servicios/form.jsp").forward(req, resp);
            return;
        }

        if (idParam != null && editarParam != null) {
            int id = Integer.parseInt(idParam);
            Servicio s = servicioDAO.buscarPorId(id);
            if (s != null) {
                req.setAttribute("servicio", s);
                req.getRequestDispatcher("/WEB-INF/views/servicios/form.jsp").forward(req, resp);
                return;
            }
        }

        // Listado
        List<Servicio> servicios = servicioDAO.listarPorTipo(tipoParam);
        req.setAttribute("servicios", servicios);
        req.setAttribute("tipoFiltro", tipoParam);
        req.getRequestDispatcher("/WEB-INF/views/servicios/lista.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String desactivarParam = req.getParameter("desactivar");

        if (idParam != null && desactivarParam != null) {
            servicioDAO.desactivar(Integer.parseInt(idParam));
            resp.sendRedirect(req.getContextPath() + "/servicios");
            return;
        }

        Servicio s = new Servicio();
        if (idParam != null && !idParam.isEmpty()) {
            s = servicioDAO.buscarPorId(Integer.parseInt(idParam));
        }

        s.setNombre(req.getParameter("nombre"));
        s.setDescripcion(req.getParameter("descripcion"));
        s.setTipo(req.getParameter("tipo"));
        
        String duracion = req.getParameter("duracionMinutos");
        if (duracion != null && !duracion.isEmpty()) {
            s.setDuracionMinutos(Integer.parseInt(duracion));
        } else {
            s.setDuracionMinutos(null);
        }
        
        s.setPrecio(new BigDecimal(req.getParameter("precio")));
        
        String activo = req.getParameter("activo");
        s.setActivo(activo != null);

        if (idParam != null && !idParam.isEmpty()) {
            servicioDAO.actualizar(s);
        } else {
            servicioDAO.insertar(s);
        }

        resp.sendRedirect(req.getContextPath() + "/servicios");
    }
}
