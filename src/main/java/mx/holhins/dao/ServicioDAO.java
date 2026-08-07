package mx.holhins.dao;

import mx.holhins.modelo.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    
    public void desactivar(int id) {
        String sql = "UPDATE servicios SET activo=FALSE WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Servicio extraerServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setTipo(rs.getString("tipo"));
        s.setDuracionMinutos(rs.getObject("duracion_minutos") != null ? rs.getInt("duracion_minutos") : null);
        s.setPrecio(rs.getBigDecimal("precio"));
        s.setActivo(rs.getBoolean("activo"));
        s.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return s;
    }
}
