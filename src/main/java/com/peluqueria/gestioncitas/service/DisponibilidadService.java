package com.peluqueria.gestioncitas.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peluqueria.gestioncitas.dto.DisponibilidadDTO;
import com.peluqueria.gestioncitas.entity.Cita;
import com.peluqueria.gestioncitas.entity.Cita.EstadoCita;
import com.peluqueria.gestioncitas.repository.CitaRepository;

/**
 * Servicio para gestionar la disponibilidad de horarios
 */
@Service
public class DisponibilidadService {
    
    @Autowired
    private CitaRepository citaRepository;
    
    // Configuración de horarios de trabajo
    private static final LocalTime HORA_INICIO_SEMANA = LocalTime.of(9, 0);
    private static final LocalTime HORA_FIN_SEMANA = LocalTime.of(20, 0);
    private static final LocalTime HORA_INICIO_SABADO = LocalTime.of(9, 0);
    private static final LocalTime HORA_FIN_SABADO = LocalTime.of(14, 0);
    private static final int INTERVALO_MINUTOS = 30;
    
    /**
     * Obtiene todas las franjas horarias de un día con su disponibilidad
     * 
     * @param fecha Fecha a consultar
     * @return Lista de franjas horarias con su estado de disponibilidad
     */
    public List<DisponibilidadDTO> obtenerDisponibilidadDia(LocalDate fecha) {
        List<DisponibilidadDTO> franjas = new ArrayList<>();
        
        // Verificar si es domingo (cerrado)
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return franjas; // Lista vacía
        }
        
        // Determinar horario según el día
        LocalTime horaInicio, horaFin;
        if (fecha.getDayOfWeek() == DayOfWeek.SATURDAY) {
            horaInicio = HORA_INICIO_SABADO;
            horaFin = HORA_FIN_SABADO;
        } else {
            horaInicio = HORA_INICIO_SEMANA;
            horaFin = HORA_FIN_SEMANA;
        }
        
        // Obtener citas del día que estén pendientes o confirmadas
        List<Cita> citasDelDia = citaRepository.findByFechaOrderByHoraAsc(fecha);
        citasDelDia = citasDelDia.stream()
.filter(c -> c.getEstado() == EstadoCita.CONFIRMADA)
            .toList();
        
        // Generar todas las franjas horarias del día
        LocalTime horaActual = horaInicio;
        while (horaActual.isBefore(horaFin)) {
            final LocalTime horaFranja = horaActual;
            
            // Verificar si esta franja está ocupada
            boolean ocupada = citasDelDia.stream()
                .anyMatch(c -> c.getHora().equals(horaFranja));
            
            DisponibilidadDTO franja = new DisponibilidadDTO(fecha, horaActual, !ocupada);
            
            // Si está ocupada, agregar información de la cita (útil para admin)
            if (ocupada) {
                Cita citaEnFranja = citasDelDia.stream()
                    .filter(c -> c.getHora().equals(horaFranja))
                    .findFirst()
                    .orElse(null);
                    
                if (citaEnFranja != null) {
                    franja.setIdCita(citaEnFranja.getIdCita());
                    franja.setClienteNombre(citaEnFranja.getCliente().getNombre() + " " + 
                    citaEnFranja.getCliente().getApellidos());
                    franja.setServicioNombre(citaEnFranja.getServicio().getNombre());
                }
            }
            
            franjas.add(franja);
            horaActual = horaActual.plusMinutes(INTERVALO_MINUTOS);
        }
        
        return franjas;
    }
    
    /**
     * Verifica si una franja horaria específica está disponible
     * 
     * @param fecha Fecha a verificar
     * @param hora Hora a verificar
     * @return true si está disponible, false si está ocupada
     */
    public boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora) {
        // Verificar si es domingo
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        
        // Verificar horario de trabajo
        if (!estaEnHorarioTrabajo(fecha, hora)) {
            return false;
        }
        
        // Verificar si ya existe una cita en ese horario
        List<Cita> citasExistentes = citaRepository.findByFechaOrderByHoraAsc(fecha);
        boolean ocupada = citasExistentes.stream()
.filter(c -> c.getEstado() == EstadoCita.CONFIRMADA)
            .anyMatch(c -> c.getHora().equals(hora));
        
        return !ocupada;
    }
    
    /**
     * Verifica si una hora está dentro del horario de trabajo
     * 
     * @param fecha Fecha a verificar
     * @param hora Hora a verificar
     * @return true si está en horario de trabajo
     */
    private boolean estaEnHorarioTrabajo(LocalDate fecha, LocalTime hora) {
        DayOfWeek dia = fecha.getDayOfWeek();
        
        if (dia == DayOfWeek.SUNDAY) {
            return false;
        }
        
        if (dia == DayOfWeek.SATURDAY) {
            return !hora.isBefore(HORA_INICIO_SABADO) && hora.isBefore(HORA_FIN_SABADO);
        }
        
        return !hora.isBefore(HORA_INICIO_SEMANA) && hora.isBefore(HORA_FIN_SEMANA);
    }
    
    /**
     * Obtiene un resumen de disponibilidad para un rango de fechas
     * Útil para mostrar en un calendario mensual
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista con información de disponibilidad por día
     */
    public List<ResumenDiaDTO> obtenerResumenDisponibilidad(LocalDate fechaInicio, LocalDate fechaFin) {
        List<ResumenDiaDTO> resumen = new ArrayList<>();
        
        LocalDate fechaActual = fechaInicio;
        while (!fechaActual.isAfter(fechaFin)) {
            List<DisponibilidadDTO> franjasDelDia = obtenerDisponibilidadDia(fechaActual);
            
            long franjasDisponibles = franjasDelDia.stream()
                .filter(DisponibilidadDTO::isDisponible)
                .count();
            
            long franjasOcupadas = franjasDelDia.stream()
                .filter(f -> !f.isDisponible())
                .count();
            
            ResumenDiaDTO resumenDia = new ResumenDiaDTO();
            resumenDia.setFecha(fechaActual);
            resumenDia.setTotalFranjas(franjasDelDia.size());
            resumenDia.setFranjasDisponibles((int) franjasDisponibles);
            resumenDia.setFranjasOcupadas((int) franjasOcupadas);
            resumenDia.setCerrado(fechaActual.getDayOfWeek() == DayOfWeek.SUNDAY);
            
            resumen.add(resumenDia);
            fechaActual = fechaActual.plusDays(1);
        }
        
        return resumen;
    }
    
    /**
     * DTO para resumen de disponibilidad por día
     */
    public static class ResumenDiaDTO {
        private LocalDate fecha;
        private int totalFranjas;
        private int franjasDisponibles;
        private int franjasOcupadas;
        private boolean cerrado;
        
        // Getters y Setters
        public LocalDate getFecha() {
            return fecha;
        }
        
        public void setFecha(LocalDate fecha) {
            this.fecha = fecha;
        }
        
        public int getTotalFranjas() {
            return totalFranjas;
        }
        
        public void setTotalFranjas(int totalFranjas) {
            this.totalFranjas = totalFranjas;
        }
        
        public int getFranjasDisponibles() {
            return franjasDisponibles;
        }
        
        public void setFranjasDisponibles(int franjasDisponibles) {
            this.franjasDisponibles = franjasDisponibles;
        }
        
        public int getFranjasOcupadas() {
            return franjasOcupadas;
        }
        
        public void setFranjasOcupadas(int franjasOcupadas) {
            this.franjasOcupadas = franjasOcupadas;
        }
        
        public boolean isCerrado() {
            return cerrado;
        }
        
        public void setCerrado(boolean cerrado) {
            this.cerrado = cerrado;
        }
    }
}