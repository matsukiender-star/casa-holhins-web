package mx.holhins.dao;

import mx.holhins.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO de usuarios del sistema. Por ahora solo necesita una operacion: buscar a
 * alguien por su nombre de usuario para el login.
 *
 * Aqui no se compara ninguna contrasena. Este DAO devuelve el usuario con su
 * hash y quien decide si la contrasena es correcta es PasswordUtil, llamado
 * desde LoginServlet. Cada quien con su responsabilidad.
 */
public class UsuarioDAO {

    private static final Logger LOG = Logger.getLogger(UsuarioDAO.class.getName());

    /**
     * Busca un usuario activo por su username.
     *
     * El filtro activo = TRUE es intencional: si a alguien se le retira el
     * acceso, deja de poder entrar sin necesidad de borrar su registro.
     * Devuelve null si no existe, y LoginServlet lo traduce a "credenciales
     * incorrectas" sin decir cual de las dos fallo.
     */
    public Usuario buscarPorUsername(String username) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = TRUE";
        
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPasswordHash(rs.getString("password_hash"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al buscar el usuario " + username, e);
        }
        return usuario;
    }
}
