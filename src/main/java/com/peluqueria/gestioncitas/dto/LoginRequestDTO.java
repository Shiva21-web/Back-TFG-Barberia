package com.peluqueria.gestioncitas.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para las peticiones de login
 * Usado tanto para login de admin como de cliente
 */
public class LoginRequestDTO {

    @NotBlank(message = "El username/nombre es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    // Constructores
    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String username, String contrasena) {
        this.username = username;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}