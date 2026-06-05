package com.peluqueria.gestioncitas.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para Servicio
 */
public class ServicioDTO {

    private Long idServicio;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 5, message = "La duración mínima es 5 minutos")
    @Max(value = 480, message = "La duración máxima es 480 minutos (8 horas)")
    private Integer duracionMinutos;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio mínimo es 0.01")
    @DecimalMax(value = "9999.99", message = "El precio máximo es 9999.99")
    @Digits(integer = 4, fraction = 2, message = "El precio debe tener máximo 4 dígitos enteros y 2 decimales")
    private BigDecimal precio;

    // Constructores
    public ServicioDTO() {
    }

    public ServicioDTO(Long idServicio, String nombre, String descripcion, 
                       Integer duracionMinutos, BigDecimal precio) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
