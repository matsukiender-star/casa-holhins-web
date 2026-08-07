package mx.holhins.servlet;

import mx.holhins.dao.DatosException;
import mx.holhins.dao.ServicioDAO;
import mx.holhins.modelo.Servicio;
import mx.holhins.util.CsrfUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador del catalogo de servicios (terapias, talleres, cursos y
 * diplomados). Mismo esquema que ClienteServlet: una sola URL que se comporta
 * distinto segun los parametros, sin nada de SQL adentro.
 *
 * Los servicios no se borran, se desactivan. La Directora necesita conservar el
 * historico de lo que se ha ofrecido aunque un servicio ya no este a la venta.
 */
@WebServlet("/servicios")
public class ServicioServlet extends HttpServlet {

    private final ServicioDAO servicioDAO = new ServicioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nuevoParam = req.getParameter("nuevo");
        String editarParam = req.getParameter("editar");
        String tipoParam = req.getParameter("tipo");
        Integer id = aEntero(req.getParameter("id"));

        if (nuevoParam != null) {
            req.getRequestDispatcher("/WEB-INF/views/servicios/form.jsp").forward(req, resp);
            return;
        }

        if (id != null && editarParam != null) {
            Servicio s = servicioDAO.buscarPorId(id);
            if (s != null) {
                req.setAttribute("servicio", s);
                req.getRequestDispatcher("/WEB-INF/views/servicios/form.jsp").forward(req, resp);
                return;
            }
        }

        // Listado, opcionalmente filtrado por tipo desde las pestanas de arriba.
        List<Servicio> servicios = servicioDAO.listarPorTipo(tipoParam);
        req.setAttribute("servicios", servicios);
        req.setAttribute("tipoFiltro", tipoParam);
        req.getRequestDispatcher("/WEB-INF/views/servicios/lista.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!CsrfUtil.esValido(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad invalido");
            return;
        }

        String desactivarParam = req.getParameter("desactivar");
        Integer id = aEntero(req.getParameter("id"));

        if (id != null && desactivarParam != null) {
            try {
                servicioDAO.desactivar(id);
            } catch (DatosException e) {
                mostrarError(req, resp, e.getMessage());
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/servicios");
            return;
        }

        Servicio s;
        if (id != null) {
            s = servicioDAO.buscarPorId(id);
            if (s == null) {
                resp.sendRedirect(req.getContextPath() + "/servicios");
                return;
            }
        } else {
            s = new Servicio();
        }

        s.setNombre(req.getParameter("nombre"));
        s.setDescripcion(req.getParameter("descripcion"));
        s.setTipo(req.getParameter("tipo"));
        s.setDuracionMinutos(aEntero(req.getParameter("duracionMinutos")));

        // El precio va en BigDecimal, nunca en double: con dinero el punto
        // flotante pierde centavos por el redondeo binario.
        BigDecimal precio = aDecimal(req.getParameter("precio"));
        if (precio == null) {
            // Sin precio valido no guardamos nada; el campo es obligatorio en la
            // tabla y de otro modo tronaria hasta abajo, en el DAO.
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El precio no es un numero valido");
            return;
        }
        s.setPrecio(precio);

        // Un checkbox no marcado sencillamente no se manda, por eso basta con
        // ver si el parametro llego o no.
        s.setActivo(req.getParameter("activo") != null);

        try {
            if (id != null) {
                servicioDAO.actualizar(s);
            } else {
                servicioDAO.insertar(s);
            }
        } catch (DatosException e) {
            // Regresamos el formulario con los datos ya capturados en vez de
            // mandarlo al listado como si se hubiera guardado.
            req.setAttribute("error", e.getMessage());
            req.setAttribute("servicio", s);
            req.getRequestDispatcher("/WEB-INF/views/servicios/form.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/servicios");
    }

    /** Repinta el catalogo con un aviso arriba cuando una escritura falla. */
    private void mostrarError(HttpServletRequest req, HttpServletResponse resp, String mensaje)
            throws ServletException, IOException {
        req.setAttribute("error", mensaje);
        req.setAttribute("servicios", servicioDAO.listarPorTipo(null));
        req.getRequestDispatcher("/WEB-INF/views/servicios/lista.jsp").forward(req, resp);
    }

    /** Igual que en ClienteServlet: null en vez de excepcion si el texto no es un entero. */
    private Integer aEntero(String valor) {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Lo mismo pero para importes. */
    private BigDecimal aDecimal(String valor) {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
