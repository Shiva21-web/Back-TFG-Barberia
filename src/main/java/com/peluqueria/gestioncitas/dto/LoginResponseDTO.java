package com.peluqueria.gestioncitas.dto;

/**
 * DTO para las respuestas de login
 * Devuelve información del usuario autenticado
 */
public class LoginResponseDTO {

    private Long id;
    private String nombre;
    private String apellidos;  // Solo para clientes
    private String email;
    private String tipoUsuario; // "ADMIN" o "CLIENTE"
    private String mensaje;

    // Constructores
    public LoginResponseDTO() {
    }

    public LoginResponseDTO(Long id, String nombre, String apellidos, String email, 
                           String tipoUsuario, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.mensaje = mensaje;
    }

    // Constructor para admin (sin apellidos)
    public LoginResponseDTO(Long id, String nombre, String email, String tipoUsuario, String mensaje) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = null;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}