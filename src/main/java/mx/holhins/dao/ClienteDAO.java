package mx.holhins.dao;

import mx.holhins.modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

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
            e.printStackTrace();
        }
        return lista;
    }
    
    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM clientes WHERE estatus != 'INACTIVO'";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
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
            ps.setString(6, c.getEstatus() != null ? c.getEstatus() : "NUEVO");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extraerCliente(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    
    public void bajaLogica(int id) {
        String sql = "UPDATE clientes SET estatus='INACTIVO' WHERE id=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
