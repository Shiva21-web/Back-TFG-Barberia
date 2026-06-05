package com.peluqueria.gestioncitas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peluqueria.gestioncitas.dto.CitaDTO;
import com.peluqueria.gestioncitas.entity.Cita;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.entity.Servicio;
import com.peluqueria.gestioncitas.repository.CitaRepository;
import com.peluqueria.gestioncitas.repository.ClienteRepository;
import com.peluqueria.gestioncitas.repository.ServicioRepository;

@Service
@Transactional
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public List<CitaDTO> obtenerTodas() {
        return citaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public CitaDTO obtenerPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        return convertirADTO(cita);
    }

    public CitaDTO crear(CitaDTO citaDTO) {
        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(citaDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Validar que el servicio existe
        Servicio servicio = servicioRepository.findById(citaDTO.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // Validar límite de citas por día (máximo 3)
        validarLimiteCitasPorDia(citaDTO.getIdCliente(), citaDTO.getFecha(), null);

        // Validar que no haya conflicto de horario considerando la duración del servicio
        validarDisponibilidadHorario(citaDTO.getFecha(), citaDTO.getHora(), servicio.getDuracionMinutos(), null);

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setServicio(servicio);
        cita.setFecha(citaDTO.getFecha());
        cita.setHora(citaDTO.getHora());
cita.setEstado(citaDTO.getEstado() != null ? citaDTO.getEstado() : Cita.EstadoCita.CONFIRMADA);
        cita.setObservaciones(citaDTO.getObservaciones());

        cita = citaRepository.save(cita);
        return convertirADTO(cita);
    }

    public CitaDTO actualizar(Long id, CitaDTO citaDTO) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

        // Validar cliente y servicio
        Cliente cliente = clienteRepository.findById(citaDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Servicio servicio = servicioRepository.findById(citaDTO.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // Si cambió la fecha, validar límite de citas en la nueva fecha
        if (!citaExistente.getFecha().equals(citaDTO.getFecha())) {
            validarLimiteCitasPorDia(citaDTO.getIdCliente(), citaDTO.getFecha(), id);
        }

        // Validar conflicto de horario si cambió fecha, hora o servicio (duración)
        if (!citaExistente.getFecha().equals(citaDTO.getFecha()) || 
            !citaExistente.getHora().equals(citaDTO.getHora()) ||
            !citaExistente.getServicio().getIdServicio().equals(citaDTO.getIdServicio())) {
            validarDisponibilidadHorario(citaDTO.getFecha(), citaDTO.getHora(), servicio.getDuracionMinutos(), id);
        }

        citaExistente.setCliente(cliente);
        citaExistente.setServicio(servicio);
        citaExistente.setFecha(citaDTO.getFecha());
        citaExistente.setHora(citaDTO.getHora());
        citaExistente.setEstado(citaDTO.getEstado());
        citaExistente.setObservaciones(citaDTO.getObservaciones());

        citaExistente = citaRepository.save(citaExistente);
        return convertirADTO(citaExistente);
    }

    public void eliminar(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
        citaRepository.deleteById(id);
    }

    public List<CitaDTO> obtenerPorFecha(LocalDate fecha) {
        return citaRepository.findByFechaOrderByHoraAsc(fecha).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> obtenerProximasCitas() {
        return citaRepository.findProximasCitas(LocalDate.now()).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<CitaDTO> obtenerPorCliente(Long idCliente) {
        return citaRepository.findByClienteIdCliente(idCliente).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private CitaDTO convertirADTO(Cita cita) {
        CitaDTO dto = new CitaDTO();
        dto.setIdCita(cita.getIdCita());
        dto.setIdCliente(cita.getCliente().getIdCliente());
        dto.setIdServicio(cita.getServicio().getIdServicio());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setEstado(cita.getEstado());
        dto.setObservaciones(cita.getObservaciones());
        dto.setFechaCreacion(cita.getFechaCreacion());
        
        // Información adicional para el frontend
        dto.setClienteNombre(cita.getCliente().getNombre() + " " + cita.getCliente().getApellidos());
        dto.setClienteTelefono(cita.getCliente().getTelefono());
        dto.setServicioNombre(cita.getServicio().getNombre());
        dto.setServicioPrecio(cita.getServicio().getPrecio().toString());
        dto.setServicioDuracion(cita.getServicio().getDuracionMinutos());
        
        return dto;
    }

    /**
     * Valida que no haya solapamiento de horarios considerando la duración de los servicios
     * 
     * @param fecha Fecha de la nueva cita
     * @param hora Hora de inicio de la nueva cita
     * @param duracionMinutos Duración del servicio en minutos
     * @param citaIdExcluir ID de cita a excluir (en caso de actualización)
     */
    private void validarDisponibilidadHorario(LocalDate fecha, java.time.LocalTime hora, 
                                               Integer duracionMinutos, Long citaIdExcluir) {
        // Obtener todas las citas activas del día
        List<Cita> citasDelDia = citaRepository.findCitasActivasByFecha(fecha);
        
        // Calcular hora de fin de la nueva cita
        java.time.LocalTime horaFin = hora.plusMinutes(duracionMinutos);
        
        for (Cita citaExistente : citasDelDia) {
            // Si es actualización, ignorar la cita que se está actualizando
            if (citaIdExcluir != null && citaExistente.getIdCita().equals(citaIdExcluir)) {
                continue;
            }
            
            // Calcular hora de fin de la cita existente
            java.time.LocalTime horaInicioExistente = citaExistente.getHora();
            java.time.LocalTime horaFinExistente = horaInicioExistente
                .plusMinutes(citaExistente.getServicio().getDuracionMinutos());
            
            // Verificar solapamiento
            // Caso 1: La nueva cita comienza durante una cita existente
            boolean iniciaEnMedio = hora.isAfter(horaInicioExistente) && hora.isBefore(horaFinExistente);
            
            // Caso 2: La nueva cita termina durante una cita existente
            boolean terminaEnMedio = horaFin.isAfter(horaInicioExistente) && horaFin.isBefore(horaFinExistente);
            
            // Caso 3: La nueva cita cubre completamente una cita existente
            boolean cubreCompleta = (hora.isBefore(horaInicioExistente) || hora.equals(horaInicioExistente)) 
                                 && (horaFin.isAfter(horaFinExistente) || horaFin.equals(horaFinExistente));
            
            // Caso 4: Misma hora de inicio
            boolean mismaHora = hora.equals(horaInicioExistente);
            
            if (iniciaEnMedio || terminaEnMedio || cubreCompleta || mismaHora) {
                throw new RuntimeException(
                    String.format("Conflicto de horario: Ya existe una cita de %s a %s. " +
                                "La cita solicitada (%s a %s) se solapa con esta.",
                                horaInicioExistente, horaFinExistente, hora, horaFin)
                );
            }
        }
    }

    /**
     * Valida que el cliente no tenga más de 3 citas en el mismo día
     * 
     * @param idCliente ID del cliente
     * @param fecha Fecha a validar
     * @param citaIdExcluir ID de cita a excluir (en caso de actualización)
     */
    private void validarLimiteCitasPorDia(Long idCliente, LocalDate fecha, Long citaIdExcluir) {
        long citasDelDia = citaRepository.contarCitasActivasClientePorFecha(idCliente, fecha);
        
        // Si es actualización y la cita ya estaba en esta fecha, no contar esa cita
        if (citaIdExcluir != null) {
            // La cita actual ya cuenta en el total, así que podemos permitir hasta 3
            if (citasDelDia > 3) {
                throw new RuntimeException(
                    "Límite de citas alcanzado: No puedes tener más de 3 citas en el mismo día"
                );
            }
        } else {
            // Nueva cita, validar que no supere el límite
            if (citasDelDia >= 3) {
                throw new RuntimeException(
                    "Límite de citas alcanzado: Ya tienes 3 citas programadas para este día. " +
                    "No puedes reservar más citas en esta fecha."
                );
            }
        }
    }
}
