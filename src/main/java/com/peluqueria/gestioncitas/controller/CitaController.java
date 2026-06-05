package com.peluqueria.gestioncitas.controller;

import com.peluqueria.gestioncitas.dto.CitaDTO;
import com.peluqueria.gestioncitas.dto.DisponibilidadDTO;
import com.peluqueria.gestioncitas.entity.Cita;
import com.peluqueria.gestioncitas.service.CitaService;
import com.peluqueria.gestioncitas.service.DisponibilidadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import com.peluqueria.gestioncitas.repository.CitaRepository;

/**
 * Controlador REST para gestionar citas
 * Endpoints: /api/citas
 */
@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CitaService citaService;
    
    @Autowired
    private DisponibilidadService disponibilidadService;

    // ============================================
    // ENDPOINTS EXISTENTES - CRUD Básico
    // ============================================
    
    @GetMapping
    public ResponseEntity<List<CitaDTO>> obtenerTodas() {
        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CitaDTO citaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(citaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CitaDTO citaDTO) {
        return ResponseEntity.ok(citaService.actualizar(id, citaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<CitaDTO>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.obtenerPorFecha(fecha));
    }

    @GetMapping("/proximas")
    public ResponseEntity<List<CitaDTO>> obtenerProximasCitas() {
        return ResponseEntity.ok(citaService.obtenerProximasCitas());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<CitaDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(citaService.obtenerPorCliente(idCliente));
    }

    // ============================================
    // NUEVOS ENDPOINTS - Sistema de Calendario
    // ============================================
    
    /**
     * Obtiene la disponibilidad de franjas horarias para un día específico
     * 
     * GET /api/citas/disponibilidad/dia?fecha=2024-04-25
     * 
     * @param fecha Fecha a consultar (formato: YYYY-MM-DD)
     * @return Lista de franjas horarias con su estado de disponibilidad
     */
    @GetMapping("/disponibilidad/dia")
    public ResponseEntity<List<DisponibilidadDTO>> obtenerDisponibilidadDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<DisponibilidadDTO> disponibilidad = disponibilidadService.obtenerDisponibilidadDia(fecha);
        return ResponseEntity.ok(disponibilidad);
    }
    
    /**
     * Obtiene un resumen de disponibilidad para un rango de fechas
     * Útil para mostrar en un calendario mensual
     * 
     * GET /api/citas/disponibilidad/rango?fechaInicio=2024-04-01&fechaFin=2024-04-30
     * 
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista con resumen de disponibilidad por día
     */
    @GetMapping("/disponibilidad/rango")
    public ResponseEntity<List<DisponibilidadService.ResumenDiaDTO>> obtenerResumenDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<DisponibilidadService.ResumenDiaDTO> resumen = 
            disponibilidadService.obtenerResumenDisponibilidad(fechaInicio, fechaFin);
        return ResponseEntity.ok(resumen);
    }
    
    /**
     * Verifica si una franja horaria específica está disponible
     * 
     * GET /api/citas/disponibilidad/verificar?fecha=2024-04-25&hora=10:00
     * 
     * @param fecha Fecha a verificar
     * @param hora Hora a verificar (formato: HH:mm)
     * @return true si está disponible, false si está ocupada
     */
    @GetMapping("/disponibilidad/verificar")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {
        boolean disponible = disponibilidadService.verificarDisponibilidad(fecha, hora);
        return ResponseEntity.ok(disponible);
    }
    
    /**
     * Permite a un cliente cancelar su propia cita
     * Cambia el estado a CANCELADA, liberando la franja horaria
     * 
     * PUT /api/citas/{id}/cancelar
     * 
     * @param id ID de la cita a cancelar
     * @return La cita actualizada con estado CANCELADA
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaDTO> cancelarCita(@PathVariable Long id) {
        var cita = citaService.obtenerPorId(id);
        Cita entidad = citaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        entidad.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepository.save(entidad);
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }
    
    /**
     * Endpoint especializado para reservar citas desde el calendario del cliente
     * Valida disponibilidad antes de crear la cita
     * 
     * POST /api/citas/reservar
     * 
     * @param citaDTO Datos de la cita a reservar
     * @return La cita creada
     */
    @PostMapping("/reservar")
    public ResponseEntity<?> reservarCita(@Valid @RequestBody CitaDTO citaDTO) {
        // Verificar disponibilidad antes de crear
        boolean disponible = disponibilidadService.verificarDisponibilidad(
            citaDTO.getFecha(), 
            citaDTO.getHora()
        );
        
        if (!disponible) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("La franja horaria seleccionada ya no está disponible. Por favor, elija otra hora.");
        }
        
        // Si está disponible, crear la cita
        CitaDTO citaCreada = citaService.crear(citaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(citaCreada);
    }
}