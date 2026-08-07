package mx.holhins.dao;

import mx.holhins.modelo.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO del catalogo de servicios (terapias, talleres, cursos y diplomados).
 *
 * Mismo patron que ClienteDAO: try-with-resources para no dejar conexiones
 * abiertas y PreparedStatement con parametros en todas las consultas.
 *
 * Los servicios tampoco se borran nunca, se desactivan (activo = FALSE), porque
 * la Directora necesita conservar el historico de lo que se ha ofrecido aunque
 * ya no este a la venta.
 */
public class ServicioDAO {

    private static final Logger LOG = Logger.getLogger(ServicioDAO.class.getName());

    /**
     * Lista los servicios activos, opcionalmente filtrados por tipo.
     *
     * Lo que se concatena aqui es un pedazo fijo de SQL escrito por nosotros
     * ("AND tipo = ?"), nunca el valor que mando el usuario: ese sigue viajando
     * como parametro. Aun asi, si algun dia hay que agregar mas filtros conviene
     * armar la consulta con un StringBuilder y una lista de parametros, porque
     * este patron se vuelve fragil rapido.
     */
    public List<Servicio> listarPorTipo(String tipo) {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE activo = TRUE " + (tipo != null && !tipo.isEmpty() ? "AND tipo = ?" : "");
        
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            if (tipo != null && !tipo.isEmpty()) {
                ps.setString(1, tipo);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerServicio(rs));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al listar servicios", e);
        }
        return lista;
    }
    
    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM servicios WHERE activo = TRUE";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al contar servicios activos", e);
        }
        return 0;
    }

    public void insertar(Servicio s) {
        String sql = "INSERT INTO servicios (nombre, descripcion, tipo, duracion_minutos, precio, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setString(3, s.getTipo());
            ps.setObject(4, s.getDuracionMinutos(), Types.INTEGER);
            ps.setBigDecimal(5, s.getPrecio());
            ps.setBoolean(6, s.getActivo() != null ? s.getActivo() : true);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al insertar servicio", e);
            throw new DatosException("No se pudo guardar el servicio.", e);
        }
    }
    
    public Servicio buscarPorId(int id) {
        String sql = "SELECT * FROM servicios WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extraerServicio(rs);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al buscar servicio", e);
        }
        return null;
    }
    
    public void actualizar(Servicio s) {
        String sql = "UPDATE servicios SET nombre=?, descripcion=?, tipo=?, duracion_minutos=?, precio=?, activo=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setString(3, s.getTipo());
            ps.setObject(4, s.getDuracionMinutos(), Types.INTEGER);
            ps.setBigDecimal(5, s.getPrecio());
            ps.setBoolean(6, s.getActivo());
            ps.setInt(7, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al actualizar servicio", e);
            throw new DatosException("No se pudieron guardar los cambios del servicio.", e);
        }
    }
    
    /** Desactiva el servicio sin borrarlo: deja de aparecer, pero se conserva. */
    public void desactivar(int id) {
        String sql = "UPDATE servicios SET activo=FALSE WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al desactivar servicio", e);
            throw new DatosException("No se pudo desactivar el servicio.", e);
        }
    }

    /** Convierte el renglon del ResultSet en un objeto Servicio. */
    private Servicio extraerServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setTipo(rs.getString("tipo"));
        // getInt() devolveria 0 si la columna es NULL, y 0 minutos no es lo mismo
        // que "sin duracion definida"; por eso preguntamos antes con getObject().
        s.setDuracionMinutos(rs.getObject("duracion_minutos") != null ? rs.getInt("duracion_minutos") : null);
        s.setPrecio(rs.getBigDecimal("precio"));
        s.setActivo(rs.getBoolean("activo"));
        s.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return s;
    }
}
