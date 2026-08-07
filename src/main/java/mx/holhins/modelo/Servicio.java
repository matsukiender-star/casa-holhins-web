package mx.holhins.modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Servicio {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private Boolean activo;
    private Timestamp fechaCreacion;

    public Servicio() {}

    public Servicio(Integer id, String nombre, String descripcion, String tipo, Integer duracionMinutos, BigDecimal precio, Boolean activo, Timestamp fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
