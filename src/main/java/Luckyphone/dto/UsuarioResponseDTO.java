package Luckyphone.dto;

import Luckyphone.entity.Usuario;

import java.time.LocalDateTime;

public class UsuarioResponseDTO {

    private Integer idUsuario;
    private String nombre;
    private String usuario;
    private Boolean estado;
    private String rol;
    private LocalDateTime fechaCreacion;

    public UsuarioResponseDTO(Usuario u) {
        this.idUsuario = u.getIdUsuario();
        this.nombre = u.getNombre();
        this.usuario = u.getUsuario();
        this.estado = u.getEstado();
        this.rol = u.getRol() != null ? u.getRol().getNombreRol() : null;
        this.fechaCreacion = u.getFechaCreacion();
    }

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getUsuario() { return usuario; }
    public Boolean getEstado() { return estado; }
    public String getRol() { return rol; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
