package mx.holhins.modelo;

import java.sql.Date;
import java.sql.Timestamp;

public class Cliente {
    private Integer id;
    private String nombreCompleto;
    private String telefono;
    private String correo;
    private Date fechaNacimiento;
    private String notas;
    private String estatus;
    private Timestamp fechaRegistro;
    private Timestamp fechaUltimaVisita;

    public Cliente() {}

    public Cliente(Integer id, String nombreCompleto, String telefono, String correo, Date fechaNacimiento, String notas, String estatus, Timestamp fechaRegistro, Timestamp fechaUltimaVisita) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.notas = notas;
        this.estatus = estatus;
        this.fechaRegistro = fechaRegistro;
        this.fechaUltimaVisita = fechaUltimaVisita;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Timestamp getFechaUltimaVisita() { return fechaUltimaVisita; }
    public void setFechaUltimaVisita(Timestamp fechaUltimaVisita) { this.fechaUltimaVisita = fechaUltimaVisita; }
}
