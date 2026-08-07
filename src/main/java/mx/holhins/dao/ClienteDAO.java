package mx.holhins.dao;

import mx.holhins.modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO de clientes. Maneja todo el acceso a la tabla `clientes` en H2.
 *
 * Uso try-with-resources en cada metodo para que la Connection se cierre solita
 * al terminar el bloque y no dejar leaks (es el equivalente del 'with' de
 * Python). El ResultSet tambien va dentro de su propio try porque cerrar el
 * PreparedStatement no siempre garantiza que el cursor quede liberado.
 *
 * Todas las consultas van con PreparedStatement y parametros con '?', nunca
 * pegando texto con +. Asi el driver manda el valor por separado del SQL y una
 * comilla en el nombre de un cliente no puede convertirse en una inyeccion.
 */
public class ClienteDAO {

    private static final Logger LOG = Logger.getLogger(ClienteDAO.class.getName());

    /**
     * Trae una pagina de clientes, del mas reciente al mas antiguo.
     *
     * Filtra los INACTIVO porque esos son los que se dieron de baja: siguen en
     * la tabla para conservar su historial, pero no se muestran.
     */
    public List<Cliente> listarPaginado(int offset, int limit) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE estatus != 'INACTIVO' ORDER BY id DESC LIMIT ? OFFSET ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerCliente(rs));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al listar clientes", e);
        }
        return lista;
    }

    /** Cuenta los clientes que no estan dados de baja (lo que sale en el dashboard). */
    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM clientes WHERE estatus != 'INACTIVO'";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al contar clientes activos", e);
        }
        return 0;
    }

    public void insertar(Cliente c) {
        String sql = "INSERT INTO clientes (nombre_completo, telefono, correo, fecha_nacimiento, notas, estatus) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombreCompleto());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setDate(4, c.getFechaNacimiento());
            ps.setString(5, c.getNotas());
            // Un cliente recien capturado entra como NUEVO si nadie dijo otra cosa.
            ps.setString(6, c.getEstatus() != null ? c.getEstatus() : "NUEVO");
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al insertar cliente", e);
            throw new DatosException("No se pudo guardar el cliente.", e);
        }
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extraerCliente(rs);
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al buscar el cliente " + id, e);
        }
        return null;
    }

    public void actualizar(Cliente c) {
        String sql = "UPDATE clientes SET nombre_completo=?, telefono=?, correo=?, fecha_nacimiento=?, notas=?, estatus=?, fecha_ultima_visita=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombreCompleto());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getCorreo());
            ps.setDate(4, c.getFechaNacimiento());
            ps.setString(5, c.getNotas());
            ps.setString(6, c.getEstatus());
            ps.setTimestamp(7, c.getFechaUltimaVisita());
            ps.setInt(8, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al actualizar el cliente " + c.getId(), e);
            throw new DatosException("No se pudieron guardar los cambios del cliente.", e);
        }
    }

    /**
     * Baja logica: marcamos el cliente como INACTIVO en vez de borrarlo.
     *
     * Nunca hacemos DELETE porque el historial del cliente sirve para el
     * seguimiento y porque un borrado no se deshace. Ojo: 'INACTIVO' tiene que
     * estar en el CHECK de la columna estatus en schema.sql, si no este UPDATE
     * falla con violacion de constraint.
     */
    public void bajaLogica(int id) {
        String sql = "UPDATE clientes SET estatus='INACTIVO' WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al dar de baja el cliente " + id, e);
            throw new DatosException("No se pudo dar de baja al cliente.", e);
        }
    }

    /**
     * Pasa el renglon del ResultSet a un objeto Cliente.
     *
     * Lo tengo aparte para no repetir este mapeo en cada consulta: si manana se
     * agrega una columna, se toca aqui y ya.
     */
    private Cliente extraerCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNombreCompleto(rs.getString("nombre_completo"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        c.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        c.setNotas(rs.getString("notas"));
        c.setEstatus(rs.getString("estatus"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        c.setFechaUltimaVisita(rs.getTimestamp("fecha_ultima_visita"));
        return c;
    }
}
