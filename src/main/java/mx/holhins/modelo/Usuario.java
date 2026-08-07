package mx.holhins.modelo;

import java.sql.Timestamp;

/**
 * Usuario que entra al sistema: la Directora (ADMIN) o la secretaria (STAFF).
 *
 * POJO sin logica. Notese que el campo se llama passwordHash y no password:
 * por aqui nunca viaja una contrasena en claro, solo el hash de bcrypt que
 * genera PasswordUtil.
 */
public class Usuario {
    private Integer id;
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private Timestamp fechaCreacion;

    public Usuario() {}

    public Usuario(Integer id, String username, String passwordHash, String nombreCompleto, String rol, Boolean activo, Timestamp fechaCreacion) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    /**
     * Nombre de pila mas apellido, para saludar en pantalla.
     *
     * Lo pidio la Directora: recibirla con los cuatro nombres ("Ana María Trejo
     * Holhins") se siente acartonado, prefiere el trato corto de la casa. Con
     * esto el dashboard dice "Bienvenid@, Ana Holhins".
     *
     * Tomamos la primera y la ultima palabra en vez de guardar un campo aparte
     * en la base, asi funciona igual para cualquier usuario que se de de alta
     * despues sin tener que capturar nada extra. Si el nombre trae una sola
     * palabra (el usuario 'staff' se llama nada mas "Secretaria"), se devuelve
     * tal cual.
     */
    public String getNombreCorto() {
        if (nombreCompleto == null) {
            return null;
        }
        String[] partes = nombreCompleto.trim().split("\\s+");
        if (partes.length < 2) {
            return nombreCompleto.trim();
        }
        return partes[0] + " " + partes[partes.length - 1];
    }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
