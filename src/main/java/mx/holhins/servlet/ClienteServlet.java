package mx.holhins.servlet;

import mx.holhins.dao.ClienteDAO;
import mx.holhins.modelo.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String nuevoParam = req.getParameter("nuevo");
        String editarParam = req.getParameter("editar");

        if (nuevoParam != null) {
            req.getRequestDispatcher("/WEB-INF/views/clientes/form.jsp").forward(req, resp);
            return;
        }

        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Cliente c = clienteDAO.buscarPorId(id);
            if (c == null) {
                resp.sendRedirect(req.getContextPath() + "/clientes");
                return;
            }
            req.setAttribute("cliente", c);
            if (editarParam != null) {
                req.getRequestDispatcher("/WEB-INF/views/clientes/form.jsp").forward(req, resp);
            } else {
                req.getRequestDispatcher("/WEB-INF/views/clientes/detalle.jsp").forward(req, resp);
            }
            return;
        }

        // Listado
        int offset = 0;
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            offset = (Integer.parseInt(pageParam) - 1) * 10;
        }
        List<Cliente> clientes = clienteDAO.listarPaginado(offset, 10);
        req.setAttribute("clientes", clientes);
        req.getRequestDispatcher("/WEB-INF/views/clientes/lista.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String bajaParam = req.getParameter("baja");

        if (idParam != null && bajaParam != null) {
            clienteDAO.bajaLogica(Integer.parseInt(idParam));
            resp.sendRedirect(req.getContextPath() + "/clientes");
            return;
        }

        Cliente c = new Cliente();
        if (idParam != null && !idParam.isEmpty()) {
            c = clienteDAO.buscarPorId(Integer.parseInt(idParam));
        }

        c.setNombreCompleto(req.getParameter("nombreCompleto"));
        c.setTelefono(req.getParameter("telefono"));
        c.setCorreo(req.getParameter("correo"));
        
        String fechaNac = req.getParameter("fechaNacimiento");
        if (fechaNac != null && !fechaNac.isEmpty()) {
            c.setFechaNacimiento(Date.valueOf(fechaNac));
        }
        
        c.setNotas(req.getParameter("notas"));
        String estatus = req.getParameter("estatus");
        if (estatus != null && !estatus.isEmpty()) {
            c.setEstatus(estatus);
        }

        if (idParam != null && !idParam.isEmpty()) {
            clienteDAO.actualizar(c);
        } else {
            clienteDAO.insertar(c);
        }

        resp.sendRedirect(req.getContextPath() + "/clientes");
    }
}
