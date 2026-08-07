package mx.holhins.servlet;

import mx.holhins.dao.ClienteDAO;
import mx.holhins.modelo.Cliente;
import mx.holhins.util.CsrfUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Controlador del CRM de clientes. Atiende el listado, el detalle, el alta, la
 * edicion y la baja, todo bajo la misma URL /clientes distinguiendo por los
 * parametros que vengan.
 *
 * Aqui no hay nada de SQL: el servlet lee lo que mando el navegador, arma el
 * objeto Cliente y le pide al DAO que lo guarde. Si algun dia cambiamos H2 por
 * MySQL, este archivo no se toca.
 */
@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    /** Cuantos clientes mostramos por pagina en el listado. */
    private static final int POR_PAGINA = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String nuevoParam = req.getParameter("nuevo");
        String editarParam = req.getParameter("editar");

        // ?nuevo= -> formulario en blanco
        if (nuevoParam != null) {
            req.getRequestDispatcher("/WEB-INF/views/clientes/form.jsp").forward(req, resp);
            return;
        }

        // ?id=N -> detalle, o formulario cargado si ademas viene ?editar=
        if (idParam != null) {
            Integer id = aEntero(idParam);
            if (id == null) {
                // Alguien escribio algo raro en la URL (?id=abc). Antes esto
                // reventaba con NumberFormatException y devolvia un 500 feo;
                // ahora simplemente lo regresamos al listado.
                resp.sendRedirect(req.getContextPath() + "/clientes");
                return;
            }
            Cliente c = clienteDAO.buscarPorId(id);
            if (c == null) {
                resp.sendRedirect(req.getContextPath() + "/clientes");
                return;
            }
            req.setAttribute("cliente", c);
            String vista = (editarParam != null)
                    ? "/WEB-INF/views/clientes/form.jsp"
                    : "/WEB-INF/views/clientes/detalle.jsp";
            req.getRequestDispatcher(vista).forward(req, resp);
            return;
        }

        // Sin parametros -> listado paginado.
        Integer pagina = aEntero(req.getParameter("page"));
        if (pagina == null || pagina < 1) {
            pagina = 1;
        }
        int offset = (pagina - 1) * POR_PAGINA;

        List<Cliente> clientes = clienteDAO.listarPaginado(offset, POR_PAGINA);
        req.setAttribute("clientes", clientes);
        req.setAttribute("paginaActual", pagina);
        req.getRequestDispatcher("/WEB-INF/views/clientes/lista.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Todo POST tiene que traer el token de la sesion. Sin esto, una pagina
        // externa podria dar de alta o borrar clientes aprovechando tu sesion.
        if (!CsrfUtil.esValido(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token de seguridad invalido");
            return;
        }

        String idParam = req.getParameter("id");
        String bajaParam = req.getParameter("baja");
        Integer id = aEntero(idParam);

        // Baja logica: no borramos el registro, solo lo marcamos INACTIVO para
        // conservar el historial del cliente.
        if (id != null && bajaParam != null) {
            clienteDAO.bajaLogica(id);
            resp.sendRedirect(req.getContextPath() + "/clientes");
            return;
        }

        // Si viene id es edicion; si no, es alta. Partimos del cliente que ya
        // esta en la base para no perder los campos que el formulario no manda
        // (por ejemplo la fecha de registro).
        Cliente c;
        if (id != null) {
            c = clienteDAO.buscarPorId(id);
            if (c == null) {
                resp.sendRedirect(req.getContextPath() + "/clientes");
                return;
            }
        } else {
            c = new Cliente();
        }

        c.setNombreCompleto(req.getParameter("nombreCompleto"));
        c.setTelefono(req.getParameter("telefono"));
        c.setCorreo(req.getParameter("correo"));

        // El input type="date" del navegador manda siempre yyyy-MM-dd, que es
        // justo el formato que espera Date.valueOf().
        String fechaNac = req.getParameter("fechaNacimiento");
        if (fechaNac != null && !fechaNac.isEmpty()) {
            try {
                c.setFechaNacimiento(Date.valueOf(fechaNac));
            } catch (IllegalArgumentException e) {
                c.setFechaNacimiento(null); // fecha con formato invalido, la ignoramos
            }
        }

        c.setNotas(req.getParameter("notas"));
        String estatus = req.getParameter("estatus");
        if (estatus != null && !estatus.isEmpty()) {
            c.setEstatus(estatus);
        }

        if (id != null) {
            clienteDAO.actualizar(c);
        } else {
            clienteDAO.insertar(c);
        }

        // Redirect y no forward: asi, si el usuario refresca la pagina, no se
        // reenvia el formulario y no se duplica el cliente (patron POST-Redirect-GET).
        resp.sendRedirect(req.getContextPath() + "/clientes");
    }

    /**
     * Convierte texto a entero devolviendo null si no se puede, en vez de
     * lanzar excepcion. Es el equivalente manual del int() dentro de un
     * try/except de Python, para no llenar el codigo de try anidados.
     */
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
}
