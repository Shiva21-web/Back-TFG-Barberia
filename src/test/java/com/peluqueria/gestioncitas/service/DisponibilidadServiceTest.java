package com.peluqueria.gestioncitas.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.peluqueria.gestioncitas.dto.DisponibilidadDTO;
import com.peluqueria.gestioncitas.entity.Cita;
import com.peluqueria.gestioncitas.entity.Cita.EstadoCita;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.entity.Servicio;
import com.peluqueria.gestioncitas.repository.CitaRepository;
import com.peluqueria.gestioncitas.service.DisponibilidadService.ResumenDiaDTO;

/**
 * Tests unitarios para DisponibilidadService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de DisponibilidadService")
class DisponibilidadServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    private Cliente cliente;
    private Servicio servicio;
    private Cita citaPendiente;
    private Cita citaCancelada;
    private LocalDate lunes;
    private LocalDate sabado;
    private LocalDate domingo;

    @BeforeEach
    void setUp() {
        // Configurar cliente
        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan");
        cliente.setApellidos("Pérez García");
        cliente.setTelefono("612345678");
        cliente.setEmail("juan@example.com");

        // Configurar servicio
        servicio = new Servicio();
        servicio.setIdServicio(1L);
        servicio.setNombre("Corte de Cabello");
        servicio.setDescripcion("Corte básico");
        servicio.setDuracionMinutos(30);
        servicio.setPrecio(new BigDecimal("15.00"));

        // Obtener fechas de prueba
        LocalDate hoy = LocalDate.now();
        lunes = hoy.with(DayOfWeek.MONDAY);
        sabado = hoy.with(DayOfWeek.SATURDAY);
        domingo = hoy.with(DayOfWeek.SUNDAY);

        // Configurar cita pendiente
        citaPendiente = new Cita();
        citaPendiente.setIdCita(1L);
        citaPendiente.setCliente(cliente);
        citaPendiente.setServicio(servicio);
        citaPendiente.setFecha(lunes);
        citaPendiente.setHora(LocalTime.of(10, 0));
        citaPendiente.setEstado(EstadoCita.CONFIRMADA);

        // Configurar cita cancelada
        citaCancelada = new Cita();
        citaCancelada.setIdCita(2L);
        citaCancelada.setCliente(cliente);
        citaCancelada.setServicio(servicio);
        citaCancelada.setFecha(lunes);
        citaCancelada.setHora(LocalTime.of(11, 0));
        citaCancelada.setEstado(EstadoCita.CANCELADA);
    }

    @Test
    @DisplayName("Debe obtener disponibilidad de un día laboral (L-V)")
    void testObtenerDisponibilidadDiaLaboral() {
        // Given
        when(citaRepository.findByFechaOrderByHoraAsc(lunes))
                .thenReturn(Arrays.asList(citaPendiente));

        // When
        List<DisponibilidadDTO> franjas = disponibilidadService.obtenerDisponibilidadDia(lunes);

        // Then
        assertThat(franjas).isNotNull();
        assertThat(franjas).isNotEmpty();
        
        // Debe haber 22 franjas (9:00 a 20:00, cada 30 min)
        assertThat(franjas.size()).isEqualTo(22);
        
        // Primera franja debe ser 9:00
        assertThat(franjas.get(0).getHora()).isEqualTo(LocalTime.of(9, 0));
        
        // Última franja debe ser 19:30
        assertThat(franjas.get(franjas.size() - 1).getHora()).isEqualTo(LocalTime.of(19, 30));
        
        // Verificar que la franja de 10:00 está ocupada
        DisponibilidadDTO franjaOcupada = franjas.stream()
                .filter(f -> f.getHora().equals(LocalTime.of(10, 0)))
                .findFirst()
                .orElse(null);
        
        assertThat(franjaOcupada).isNotNull();
        assertThat(franjaOcupada.isDisponible()).isFalse();
        assertThat(franjaOcupada.getClienteNombre()).isEqualTo("Juan Pérez García");
        
        verify(citaRepository, times(1)).findByFechaOrderByHoraAsc(lunes);
    }

    @Test
    @DisplayName("Debe obtener disponibilidad de sábado con horario reducido")
    void testObtenerDisponibilidadSabado() {
        // Given
        when(citaRepository.findByFechaOrderByHoraAsc(sabado))
                .thenReturn(Collections.emptyList());

        // When
        List<DisponibilidadDTO> franjas = disponibilidadService.obtenerDisponibilidadDia(sabado);

        // Then
        assertThat(franjas).isNotNull();
        assertThat(franjas).isNotEmpty();
        
        // Debe haber 10 franjas (9:00 a 14:00, cada 30 min)
        assertThat(franjas.size()).isEqualTo(10);
        
        // Primera franja debe ser 9:00
        assertThat(franjas.get(0).getHora()).isEqualTo(LocalTime.of(9, 0));
        
        // Última franja debe ser 13:30
        assertThat(franjas.get(franjas.size() - 1).getHora()).isEqualTo(LocalTime.of(13, 30));
        
        // Todas deben estar disponibles
        long disponibles = franjas.stream().filter(DisponibilidadDTO::isDisponible).count();
        assertThat(disponibles).isEqualTo(10);
        
        verify(citaRepository, times(1)).findByFechaOrderByHoraAsc(sabado);
    }

    @Test
    @DisplayName("Debe retornar lista vacía para domingo (cerrado)")
    void testObtenerDisponibilidadDomingo() {
        // Given - no se llama al repository para domingos
        
        // When
        List<DisponibilidadDTO> franjas = disponibilidadService.obtenerDisponibilidadDia(domingo);

        // Then
        assertThat(franjas).isNotNull();
        assertThat(franjas).isEmpty();
        
        verify(citaRepository, never()).findByFechaOrderByHoraAsc(any());
    }

    @Test
    @DisplayName("Debe verificar que una franja está disponible")
    void testVerificarDisponibilidadFranjaLibre() {
        // Given
        LocalDate fecha = lunes;
        LocalTime hora = LocalTime.of(15, 0);
        
        when(citaRepository.findByFechaOrderByHoraAsc(fecha))
                .thenReturn(Arrays.asList(citaPendiente)); // Solo hay cita a las 10:00

        // When
        boolean disponible = disponibilidadService.verificarDisponibilidad(fecha, hora);

        // Then
        assertThat(disponible).isTrue();
        verify(citaRepository, times(1)).findByFechaOrderByHoraAsc(fecha);
    }

    @Test
    @DisplayName("Debe verificar que una franja está ocupada")
    void testVerificarDisponibilidadFranjaOcupada() {
        // Given
        LocalDate fecha = lunes;
        LocalTime hora = LocalTime.of(10, 0);
        
        when(citaRepository.findByFechaOrderByHoraAsc(fecha))
                .thenReturn(Arrays.asList(citaPendiente));

        // When
        boolean disponible = disponibilidadService.verificarDisponibilidad(fecha, hora);

        // Then
        assertThat(disponible).isFalse();
        verify(citaRepository, times(1)).findByFechaOrderByHoraAsc(fecha);
    }

    @Test
    @DisplayName("Debe retornar false para horario fuera del horario laboral")
    void testVerificarDisponibilidadFueraDeHorario() {
        // Given
        LocalDate fecha = lunes;
        LocalTime horaAntes = LocalTime.of(8, 0); // Antes de las 9:00
        LocalTime horaDespues = LocalTime.of(21, 0); // Después de las 20:00

        // When
        boolean disponibleAntes = disponibilidadService.verificarDisponibilidad(fecha, horaAntes);
        boolean disponibleDespues = disponibilidadService.verificarDisponibilidad(fecha, horaDespues);

        // Then
        assertThat(disponibleAntes).isFalse();
        assertThat(disponibleDespues).isFalse();
        
        // No debe llamar al repository si está fuera de horario
        verify(citaRepository, never()).findByFechaOrderByHoraAsc(any());
    }

    @Test
    @DisplayName("Debe retornar false para domingo (cerrado)")
    void testVerificarDisponibilidadDomingo() {
        // Given
        LocalDate fecha = domingo;
        LocalTime hora = LocalTime.of(10, 0);

        // When
        boolean disponible = disponibilidadService.verificarDisponibilidad(fecha, hora);

        // Then
        assertThat(disponible).isFalse();
        verify(citaRepository, never()).findByFechaOrderByHoraAsc(any());
    }

    @Test
    @DisplayName("Debe obtener resumen de disponibilidad para rango de fechas")
    void testObtenerResumenDisponibilidad() {
        // Given
        LocalDate inicio = lunes;
        LocalDate fin = lunes.plusDays(2); // Lunes, Martes, Miércoles
        
        // Simular que solo el lunes tiene una cita
        when(citaRepository.findByFechaOrderByHoraAsc(lunes))
                .thenReturn(Arrays.asList(citaPendiente));
        when(citaRepository.findByFechaOrderByHoraAsc(lunes.plusDays(1)))
                .thenReturn(Collections.emptyList());
        when(citaRepository.findByFechaOrderByHoraAsc(lunes.plusDays(2)))
                .thenReturn(Collections.emptyList());

        // When
        List<ResumenDiaDTO> resumen = disponibilidadService.obtenerResumenDisponibilidad(inicio, fin);

        // Then
        assertThat(resumen).isNotNull();
        assertThat(resumen).hasSize(3);
        
        // Verificar el lunes (tiene 1 cita)
        ResumenDiaDTO resumenLunes = resumen.get(0);
        assertThat(resumenLunes.getFecha()).isEqualTo(lunes);
        assertThat(resumenLunes.getTotalFranjas()).isEqualTo(22);
        assertThat(resumenLunes.getFranjasOcupadas()).isEqualTo(1);
        assertThat(resumenLunes.getFranjasDisponibles()).isEqualTo(21);
        assertThat(resumenLunes.isCerrado()).isFalse();
        
        verify(citaRepository, times(3)).findByFechaOrderByHoraAsc(any());
    }

    @Test
    @DisplayName("Las citas CANCELADAS o COMPLETADAS no deben ocupar franjas")
    void testCitasCanceladasNoOcupanFranjas() {
        // Given
        when(citaRepository.findByFechaOrderByHoraAsc(lunes))
                .thenReturn(Arrays.asList(citaCancelada)); // Solo cita cancelada

        // When
        List<DisponibilidadDTO> franjas = disponibilidadService.obtenerDisponibilidadDia(lunes);

        // Then
        assertThat(franjas).isNotNull();
        
        // La franja de 11:00 debe estar disponible (la cita está cancelada)
        DisponibilidadDTO franja11 = franjas.stream()
                .filter(f -> f.getHora().equals(LocalTime.of(11, 0)))
                .findFirst()
                .orElse(null);
        
        assertThat(franja11).isNotNull();
        assertThat(franja11.isDisponible()).isTrue();
        
        verify(citaRepository, times(1)).findByFechaOrderByHoraAsc(lunes);
    }

    @Test
    @DisplayName("Debe incluir información de la cita en franjas ocupadas")
    void testFranjasOcupadasIncluyenInfoCita() {
        // Given
        when(citaRepository.findByFechaOrderByHoraAsc(lunes))
                .thenReturn(Arrays.asList(citaPendiente));

        // When
        List<DisponibilidadDTO> franjas = disponibilidadService.obtenerDisponibilidadDia(lunes);

        // Then
        DisponibilidadDTO franjaOcupada = franjas.stream()
                .filter(f -> f.getHora().equals(LocalTime.of(10, 0)))
                .findFirst()
                .orElse(null);
        
        assertThat(franjaOcupada).isNotNull();
        assertThat(franjaOcupada.isDisponible()).isFalse();
        assertThat(franjaOcupada.getIdCita()).isEqualTo(1L);
        assertThat(franjaOcupada.getClienteNombre()).isEqualTo("Juan Pérez García");
        assertThat(franjaOcupada.getServicioNombre()).isEqualTo("Corte de Cabello");
        
        verify(citaRepository, times(1)).findByFechaOrderByHoraAsc(lunes);
    }
}
