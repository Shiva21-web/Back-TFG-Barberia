package com.peluqueria.gestioncitas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para representar la disponibilidad de una franja horaria
 */
public class DisponibilidadDTO {
    
    private LocalDate fecha;
    private LocalTime hora;
    private boolean disponible;
    private Long idCita; // ID de la cita si está reservada
    private String clienteNombre; // Nombre del cliente (solo para admin)
    private String servicioNombre; // Nombre del servicio (solo para admin)
    
    // Constructores
    public DisponibilidadDTO() {
    }
    
    public DisponibilidadDTO(LocalDate fecha, LocalTime hora, boolean disponible) {
        this.fecha = fecha;
        this.hora = hora;
        this.disponible = disponible;
    }
    
    public DisponibilidadDTO(LocalDate fecha, LocalTime hora, boolean disponible, 
                           Long idCita, String clienteNombre, String servicioNombre) {
        this.fecha = fecha;
        this.hora = hora;
        this.disponible = disponible;
        this.idCita = idCita;
        this.clienteNombre = clienteNombre;
        this.servicioNombre = servicioNombre;
    }
    
    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public LocalTime getHora() {
        return hora;
    }
    
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public Long getIdCita() {
        return idCita;
    }
    
    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }
    
    public String getClienteNombre() {
        return clienteNombre;
    }
    
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }
    
    public String getServicioNombre() {
        return servicioNombre;
    }
    
    public void setServicioNombre(String servicioNombre) {
        this.servicioNombre = servicioNombre;
    }
}