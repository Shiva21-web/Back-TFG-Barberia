package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.CitaDTO;
import com.peluqueria.gestioncitas.entity.Cita;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.entity.Servicio;
import com.peluqueria.gestioncitas.repository.CitaRepository;
import com.peluqueria.gestioncitas.repository.ClienteRepository;
import com.peluqueria.gestioncitas.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CitaService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de CitaService")
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private CitaService citaService;

    private Cliente cliente1;
    private Servicio servicio1;
    private Cita cita1;
    private Cita cita2;
    private CitaDTO citaDTO1;

    @BeforeEach
    void setUp() {
        // Configurar cliente de prueba
        cliente1 = new Cliente();
        cliente1.setIdCliente(1L);
        cliente1.setNombre("Juan");
        cliente1.setApellidos("Pérez García");
        cliente1.setTelefono("612345678");
        cliente1.setEmail("juan@example.com");
        cliente1.setFechaRegistro(LocalDateTime.now());

        // Configurar servicio de prueba
        servicio1 = new Servicio();
        servicio1.setIdServicio(1L);
        servicio1.setNombre("Corte de Cabello");
        servicio1.setDescripcion("Corte de cabello clásico");
        servicio1.setDuracionMinutos(30);
        servicio1.setPrecio(new BigDecimal("15.00"));

        // Configurar cita de prueba 1
        cita1 = new Cita();
        cita1.setIdCita(1L);
        cita1.setCliente(cliente1);
        cita1.setServicio(servicio1);
        cita1.setFecha(LocalDate.now().plusDays(1));
        cita1.setHora(LocalTime.of(10, 0));
        cita1.setEstado(Cita.EstadoCita.CONFIRMADA);
        cita1.setObservaciones("Primera cita");
        cita1.setFechaCreacion(LocalDateTime.now());

        // Configurar cita de prueba 2
        cita2 = new Cita();
        cita2.setIdCita(2L);
        cita2.setCliente(cliente1);
        cita2.setServicio(servicio1);
        cita2.setFecha(LocalDate.now().plusDays(2));
        cita2.setHora(LocalTime.of(11, 0));
        cita2.setEstado(Cita.EstadoCita.CONFIRMADA);
        cita2.setObservaciones("Segunda cita");
        cita2.setFechaCreacion(LocalDateTime.now());

        // Configurar CitaDTO de prueba
        citaDTO1 = new CitaDTO();
        citaDTO1.setIdCita(1L);
        citaDTO1.setIdCliente(1L);
        citaDTO1.setIdServicio(1L);
        citaDTO1.setFecha(LocalDate.now().plusDays(1));
        citaDTO1.setHora(LocalTime.of(10, 0));
        citaDTO1.setEstado(Cita.EstadoCita.CONFIRMADA);
        citaDTO1.setObservaciones("Primera cita");
    }

    @Test
    @DisplayName("Debe obtener todas las citas exitosamente")
    void testObtenerTodasLasCitas() {
        // Given
        List<Cita> citas = Arrays.asList(cita1, cita2);
        when(citaRepository.findAll()).thenReturn(citas);

        // When
        List<CitaDTO> resultado = citaService.obtenerTodas();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getIdCita()).isEqualTo(1L);
        assertThat(resultado.get(0).getClienteNombre()).isEqualTo("Juan Pérez García");
        assertThat(resultado.get(0).getServicioNombre()).isEqualTo("Corte de Cabello");
        verify(citaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una cita por ID exitosamente")
    void testObtenerCitaPorId() {
        // Given
        Long idCita = 1L;
        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita1));

        // When
        CitaDTO resultado = citaService.obtenerPorId(idCita);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCita()).isEqualTo(idCita);
        assertThat(resultado.getIdCliente()).isEqualTo(1L);
        assertThat(resultado.getIdServicio()).isEqualTo(1L);
        assertThat(resultado.getClienteNombre()).isEqualTo("Juan Pérez García");
        assertThat(resultado.getServicioNombre()).isEqualTo("Corte de Cabello");
        verify(citaRepository, times(1)).findById(idCita);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la cita no existe")
    void testObtenerCitaPorIdNoExiste() {
        // Given
        Long idCita = 999L;
        when(citaRepository.findById(idCita)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> citaService.obtenerPorId(idCita))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cita no encontrada con ID: 999");
        verify(citaRepository, times(1)).findById(idCita);
    }

    @Test
    @DisplayName("Debe crear una cita exitosamente")
    void testCrearCita() {
        // Given
        CitaDTO nuevaCitaDTO = new CitaDTO();
        nuevaCitaDTO.setIdCliente(1L);
        nuevaCitaDTO.setIdServicio(1L);
        nuevaCitaDTO.setFecha(LocalDate.now().plusDays(3));
        nuevaCitaDTO.setHora(LocalTime.of(15, 0));
        nuevaCitaDTO.setObservaciones("Nueva cita");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio1));
        when(citaRepository.findCitasActivasByFecha(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(cita1);

        // When
        CitaDTO resultado = citaService.crear(nuevaCitaDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCliente()).isEqualTo(1L);
        assertThat(resultado.getIdServicio()).isEqualTo(1L);
        verify(clienteRepository).findById(1L);
        verify(servicioRepository).findById(1L);
        verify(citaRepository).findCitasActivasByFecha(any(LocalDate.class));
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cita con cliente no existente")
    void testCrearCitaConClienteNoExistente() {
        // Given
        CitaDTO nuevaCitaDTO = new CitaDTO();
        nuevaCitaDTO.setIdCliente(999L);
        nuevaCitaDTO.setIdServicio(1L);
        nuevaCitaDTO.setFecha(LocalDate.now().plusDays(1));
        nuevaCitaDTO.setHora(LocalTime.of(10, 0));

        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> citaService.crear(nuevaCitaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado");
        
        verify(clienteRepository).findById(999L);
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cita con servicio no existente")
    void testCrearCitaConServicioNoExistente() {
        // Given
        CitaDTO nuevaCitaDTO = new CitaDTO();
        nuevaCitaDTO.setIdCliente(1L);
        nuevaCitaDTO.setIdServicio(999L);
        nuevaCitaDTO.setFecha(LocalDate.now().plusDays(1));
        nuevaCitaDTO.setHora(LocalTime.of(10, 0));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> citaService.crear(nuevaCitaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio no encontrado");
        
        verify(clienteRepository).findById(1L);
        verify(servicioRepository).findById(999L);
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cita con conflicto de horario")
    void testCrearCitaConConflictoHorario() {
        // Given
        CitaDTO nuevaCitaDTO = new CitaDTO();
        nuevaCitaDTO.setIdCliente(1L);
        nuevaCitaDTO.setIdServicio(1L);
        nuevaCitaDTO.setFecha(LocalDate.now().plusDays(1));
        nuevaCitaDTO.setHora(LocalTime.of(10, 15)); // Se solapa con cita1 que es de 10:00 a 10:30

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio1));
        when(citaRepository.findCitasActivasByFecha(LocalDate.now().plusDays(1)))
                .thenReturn(Arrays.asList(cita1));

        // When & Then
        assertThatThrownBy(() -> citaService.crear(nuevaCitaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Conflicto de horario");
        
        verify(clienteRepository).findById(1L);
        verify(servicioRepository).findById(1L);
        verify(citaRepository).findCitasActivasByFecha(LocalDate.now().plusDays(1));
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe actualizar una cita exitosamente")
    void testActualizarCita() {
        // Given
        Long idCita = 1L;
        CitaDTO citaActualizadaDTO = new CitaDTO();
        citaActualizadaDTO.setIdCliente(1L);
        citaActualizadaDTO.setIdServicio(1L);
        citaActualizadaDTO.setFecha(LocalDate.now().plusDays(1));
        citaActualizadaDTO.setHora(LocalTime.of(16, 0)); // Cambio de hora
        citaActualizadaDTO.setEstado(Cita.EstadoCita.CONFIRMADA);
        citaActualizadaDTO.setObservaciones("Cita actualizada");

        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita1));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio1));
        when(citaRepository.findCitasActivasByFecha(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(cita1);

        // When
        CitaDTO resultado = citaService.actualizar(idCita, citaActualizadaDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCita()).isEqualTo(idCita);
        verify(citaRepository).findById(idCita);
        verify(clienteRepository).findById(1L);
        verify(servicioRepository).findById(1L);
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe eliminar una cita exitosamente")
    void testEliminarCita() {
        // Given
        Long idCita = 1L;
        when(citaRepository.existsById(idCita)).thenReturn(true);
        doNothing().when(citaRepository).deleteById(idCita);

        // When
        citaService.eliminar(idCita);

        // Then
        verify(citaRepository).existsById(idCita);
        verify(citaRepository).deleteById(idCita);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cita inexistente")
    void testEliminarCitaInexistente() {
        // Given
        Long idCita = 999L;
        when(citaRepository.existsById(idCita)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> citaService.eliminar(idCita))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cita no encontrada con ID: 999");
        
        verify(citaRepository).existsById(idCita);
        verify(citaRepository, never()).deleteById(idCita);
    }

    @Test
    @DisplayName("Debe obtener citas por fecha")
    void testObtenerCitasPorFecha() {
        // Given
        LocalDate fecha = LocalDate.now().plusDays(1);
        List<Cita> citasDelDia = Arrays.asList(cita1);
        when(citaRepository.findByFechaOrderByHoraAsc(fecha)).thenReturn(citasDelDia);

        // When
        List<CitaDTO> resultado = citaService.obtenerPorFecha(fecha);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getFecha()).isEqualTo(fecha);
        verify(citaRepository).findByFechaOrderByHoraAsc(fecha);
    }

    @Test
    @DisplayName("Debe obtener próximas citas")
    void testObtenerProximasCitas() {
        // Given
        List<Cita> proximasCitas = Arrays.asList(cita1, cita2);
        when(citaRepository.findProximasCitas(any(LocalDate.class))).thenReturn(proximasCitas);

        // When
        List<CitaDTO> resultado = citaService.obtenerProximasCitas();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getFecha()).isAfter(LocalDate.now().minusDays(1));
        verify(citaRepository).findProximasCitas(any(LocalDate.class));
    }

    @Test
    @DisplayName("Debe obtener citas por cliente")
    void testObtenerCitasPorCliente() {
        // Given
        Long idCliente = 1L;
        List<Cita> citasDelCliente = Arrays.asList(cita1, cita2);
        when(citaRepository.findByClienteIdCliente(idCliente)).thenReturn(citasDelCliente);

        // When
        List<CitaDTO> resultado = citaService.obtenerPorCliente(idCliente);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getIdCliente()).isEqualTo(idCliente);
        assertThat(resultado.get(1).getIdCliente()).isEqualTo(idCliente);
        verify(citaRepository).findByClienteIdCliente(idCliente);
    }

    @Test
    @DisplayName("Debe permitir crear cita si el cliente tiene menos de 3 citas en el día")
    void testCrearCitaConMenosDe3CitasEnElDia() {
        // Given
        CitaDTO nuevaCitaDTO = new CitaDTO();
        nuevaCitaDTO.setIdCliente(1L);
        nuevaCitaDTO.setIdServicio(1L);
        nuevaCitaDTO.setFecha(LocalDate.now().plusDays(3));
        nuevaCitaDTO.setHora(LocalTime.of(15, 0));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio1));
        when(citaRepository.contarCitasActivasClientePorFecha(1L, nuevaCitaDTO.getFecha()))
                .thenReturn(2L); // Ya tiene 2 citas, puede agregar 1 más
        when(citaRepository.findCitasActivasByFecha(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(citaRepository.save(any(Cita.class))).thenReturn(cita1);

        // When
        CitaDTO resultado = citaService.crear(nuevaCitaDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(citaRepository).contarCitasActivasClientePorFecha(1L, nuevaCitaDTO.getFecha());
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente ya tiene 3 citas en el día")
    void testCrearCitaConLimiteAlcanzado() {
        // Given
        CitaDTO nuevaCitaDTO = new CitaDTO();
        nuevaCitaDTO.setIdCliente(1L);
        nuevaCitaDTO.setIdServicio(1L);
        nuevaCitaDTO.setFecha(LocalDate.now().plusDays(3));
        nuevaCitaDTO.setHora(LocalTime.of(15, 0));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio1));
        when(citaRepository.contarCitasActivasClientePorFecha(1L, nuevaCitaDTO.getFecha()))
                .thenReturn(3L); // Ya tiene 3 citas

        // When & Then
        assertThatThrownBy(() -> citaService.crear(nuevaCitaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Límite de citas alcanzado")
                .hasMessageContaining("Ya tienes 3 citas programadas");

        verify(citaRepository).contarCitasActivasClientePorFecha(1L, nuevaCitaDTO.getFecha());
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe validar límite al actualizar cita cambiando de fecha")
    void testActualizarCitaCambiandoFechaConLimiteAlcanzado() {
        // Given
        Long idCita = 1L;
        CitaDTO citaActualizadaDTO = new CitaDTO();
        citaActualizadaDTO.setIdCliente(1L);
        citaActualizadaDTO.setIdServicio(1L);
        citaActualizadaDTO.setFecha(LocalDate.now().plusDays(5)); // Nueva fecha
        citaActualizadaDTO.setHora(LocalTime.of(16, 0));
        citaActualizadaDTO.setEstado(Cita.EstadoCita.CONFIRMADA);

        when(citaRepository.findById(idCita)).thenReturn(Optional.of(cita1));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente1));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio1));
        when(citaRepository.contarCitasActivasClientePorFecha(1L, citaActualizadaDTO.getFecha()))
                .thenReturn(4L); // La nueva fecha ya tiene 4 citas (más que 3)

        // When & Then
        assertThatThrownBy(() -> citaService.actualizar(idCita, citaActualizadaDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Límite de citas alcanzado");

        verify(citaRepository).contarCitasActivasClientePorFecha(1L, citaActualizadaDTO.getFecha());
        verify(citaRepository, never()).save(any(Cita.class));
    }
}
