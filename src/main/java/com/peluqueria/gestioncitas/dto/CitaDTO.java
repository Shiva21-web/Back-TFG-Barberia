package com.peluqueria.gestioncitas.dto;

import com.peluqueria.gestioncitas.entity.Cita.EstadoCita;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * DTO para Cita
 * Incluye toda la información necesaria para crear y mostrar citas
 */
public class CitaDTO {

    /**
     * ID de la cita
     */
    private Long idCita;

    /**
     * ID del cliente
     */
    @NotNull(message = "El cliente es obligatorio")
    private Long idCliente;

    /**
     * ID del servicio
     */
    @NotNull(message = "El servicio es obligatorio")
    private Long idServicio;

    /**
     * Fecha de la cita
     * Nota: Se removió @FutureOrPresent para permitir actualizar citas pasadas
     * (ej: marcar como COMPLETADA después de realizarla)
     */
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    /**
     * Hora de la cita
     */
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    /**
     * Estado de la cita
     */
    private EstadoCita estado;

    /**
     * Observaciones opcionales
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    /**
     * Fecha de creación (solo lectura)
     */
    private LocalDateTime fechaCreacion;

    // Campos adicionales para mostrar información completa en el frontend
    
    /**
     * Nombre completo del cliente (para mostrar en listas)
     */
    private String clienteNombre;

    /**
     * Teléfono del cliente
     */
    private String clienteTelefono;

    /**
     * Nombre del servicio
     */
    private String servicioNombre;

    /**
     * Precio del servicio
     */
    private String servicioPrecio;

    /**
     * Duración del servicio en minutos
     */
    private Integer servicioDuracion;

    // Constructores
    public CitaDTO() {
    }

    public CitaDTO(Long idCita, Long idCliente, Long idServicio, LocalDate fecha, 
                   LocalTime hora, EstadoCita estado, String observaciones, 
                   LocalDateTime fechaCreacion, String clienteNombre, String clienteTelefono,
                   String servicioNombre, String servicioPrecio, Integer servicioDuracion) {
        this.idCita = idCita;
        this.idCliente = idCliente;
        this.idServicio = idServicio;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
        this.clienteNombre = clienteNombre;
        this.clienteTelefono = clienteTelefono;
        this.servicioNombre = servicioNombre;
        this.servicioPrecio = servicioPrecio;
        this.servicioDuracion = servicioDuracion;
    }

    // Getters y Setters
    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

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

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public String getServicioNombre() {
        return servicioNombre;
    }

    public void setServicioNombre(String servicioNombre) {
        this.servicioNombre = servicioNombre;
    }

    public String getServicioPrecio() {
        return servicioPrecio;
    }

    public void setServicioPrecio(String servicioPrecio) {
        this.servicioPrecio = servicioPrecio;
    }

    public Integer getServicioDuracion() {
        return servicioDuracion;
    }

    public void setServicioDuracion(Integer servicioDuracion) {
        this.servicioDuracion = servicioDuracion;
    }
}