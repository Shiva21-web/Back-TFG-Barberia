package com.peluqueria.gestioncitas.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peluqueria.gestioncitas.dto.CitaDTO;
import com.peluqueria.gestioncitas.dto.DisponibilidadDTO;
import com.peluqueria.gestioncitas.entity.Cita.EstadoCita;
import com.peluqueria.gestioncitas.repository.CitaRepository;
import com.peluqueria.gestioncitas.service.CitaService;
import com.peluqueria.gestioncitas.service.DisponibilidadService;

/**
 * Tests de integración para CitaController usando MockMVC Incluye tests para
 * endpoints de disponibilidad y calendario
 */
@WebMvcTest(CitaController.class)
@DisplayName("Tests de CitaController")

class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CitaService citaService;

    @MockBean
    private CitaRepository citaRepository;

    @MockBean
    private DisponibilidadService disponibilidadService;

    private CitaDTO citaDTO1;
    private CitaDTO citaDTO2;

    @BeforeEach
    void setUp() {
        citaDTO1 = new CitaDTO();
        citaDTO1.setIdCita(1L);
        citaDTO1.setIdCliente(1L);
        citaDTO1.setIdServicio(1L);
        citaDTO1.setFecha(LocalDate.now().plusDays(1));
        citaDTO1.setHora(LocalTime.of(10, 0));
        citaDTO1.setEstado(EstadoCita.CONFIRMADA);
        citaDTO1.setClienteNombre("Juan Pérez García");
        citaDTO1.setServicioNombre("Corte de Cabello");

        citaDTO2 = new CitaDTO();
        citaDTO2.setIdCita(2L);
        citaDTO2.setIdCliente(1L);
        citaDTO2.setIdServicio(2L);
        citaDTO2.setFecha(LocalDate.now().plusDays(2));
        citaDTO2.setHora(LocalTime.of(11, 0));
        citaDTO2.setEstado(EstadoCita.CONFIRMADA);
        citaDTO2.setClienteNombre("Juan Pérez García");
        citaDTO2.setServicioNombre("Tinte Completo");
    }

    @Test
    @DisplayName("GET /api/citas - Debe obtener todas las citas")
    void testGetTodasLasCitas() throws Exception {
        // Given
        List<CitaDTO> citas = Arrays.asList(citaDTO1, citaDTO2);
        when(citaService.obtenerTodas()).thenReturn(citas);

        // When & Then
        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clienteNombre", is("Juan Pérez García")))
                .andExpect(jsonPath("$[1].estado", is("CONFIRMADA")));

        verify(citaService, times(1)).obtenerTodas();
    }

    @Test
    @DisplayName("GET /api/citas/{id} - Debe obtener una cita por ID")
    void testGetCitaPorId() throws Exception {
        // Given
        Long idCita = 1L;
        when(citaService.obtenerPorId(idCita)).thenReturn(citaDTO1);

        // When & Then
        mockMvc.perform(get("/api/citas/{id}", idCita))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idCita", is(1)))
                .andExpect(jsonPath("$.clienteNombre", is("Juan Pérez García")))
                .andExpect(jsonPath("$.servicioNombre", is("Corte de Cabello")));

verify(citaService, times(1)).obtenerPorId(idCita);
    }

    @Test
    @DisplayName("POST /api/citas - Debe crear una cita exitosamente")
    void testCrearCita() throws Exception {
        // Given
        CitaDTO nuevaCita = new CitaDTO();
        nuevaCita.setIdCliente(1L);
        nuevaCita.setIdServicio(1L);
        nuevaCita.setFecha(LocalDate.now().plusDays(3));
        nuevaCita.setHora(LocalTime.of(15, 0));
        nuevaCita.setObservaciones("Nueva cita");

        CitaDTO citaCreada = new CitaDTO();
        citaCreada.setIdCita(3L);
        citaCreada.setIdCliente(1L);
        citaCreada.setIdServicio(1L);
        citaCreada.setFecha(LocalDate.now().plusDays(3));
        citaCreada.setHora(LocalTime.of(15, 0));
        citaCreada.setEstado(EstadoCita.CONFIRMADA);

        when(citaService.crear(any(CitaDTO.class))).thenReturn(citaCreada);

        // When & Then
        mockMvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaCita)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCita", is(3)))
                .andExpect(jsonPath("$.estado", is("CONFIRMADA")));

        verify(citaService, times(1)).crear(any(CitaDTO.class));
    }

    @Test
    @DisplayName("PUT /api/citas/{id} - Debe actualizar una cita")
    void testActualizarCita() throws Exception {
        // Given
        Long idCita = 1L;
        CitaDTO citaActualizada = new CitaDTO();
        citaActualizada.setIdCliente(1L);
        citaActualizada.setIdServicio(1L);
        citaActualizada.setFecha(LocalDate.now().plusDays(1));
        citaActualizada.setHora(LocalTime.of(16, 0));
        citaActualizada.setEstado(EstadoCita.CONFIRMADA);

        when(citaService.actualizar(eq(idCita), any(CitaDTO.class))).thenReturn(citaActualizada);

        // When & Then
        mockMvc.perform(put("/api/citas/{id}", idCita)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(citaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CONFIRMADA")));

        verify(citaService, times(1)).actualizar(eq(idCita), any(CitaDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/citas/{id} - Debe eliminar una cita")
    void testEliminarCita() throws Exception {
        // Given
        Long idCita = 1L;
        doNothing().when(citaService).eliminar(idCita);

        // When & Then
        mockMvc.perform(delete("/api/citas/{id}", idCita))
                .andExpect(status().isNoContent());

        verify(citaService, times(1)).eliminar(idCita);
    }

    @Test
    @DisplayName("GET /api/citas/fecha/{fecha} - Debe obtener citas por fecha")
    void testGetCitasPorFecha() throws Exception {
        // Given
        LocalDate fecha = LocalDate.now().plusDays(1);
        List<CitaDTO> citasDelDia = Arrays.asList(citaDTO1);
        when(citaService.obtenerPorFecha(fecha)).thenReturn(citasDelDia);

        // When & Then
        mockMvc.perform(get("/api/citas/fecha/{fecha}", fecha.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fecha", is(fecha.toString())));

        verify(citaService, times(1)).obtenerPorFecha(fecha);
    }

    @Test
    @DisplayName("GET /api/citas/proximas - Debe obtener próximas citas")
    void testGetProximasCitas() throws Exception {
        // Given
        List<CitaDTO> proximasCitas = Arrays.asList(citaDTO1, citaDTO2);
        when(citaService.obtenerProximasCitas()).thenReturn(proximasCitas);

        // When & Then
        mockMvc.perform(get("/api/citas/proximas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(citaService, times(1)).obtenerProximasCitas();
    }

    @Test
    @DisplayName("GET /api/citas/cliente/{id} - Debe obtener citas por cliente")
    void testGetCitasPorCliente() throws Exception {
        // Given
        Long idCliente = 1L;
        List<CitaDTO> citasDelCliente = Arrays.asList(citaDTO1, citaDTO2);
        when(citaService.obtenerPorCliente(idCliente)).thenReturn(citasDelCliente);

        // When & Then
        mockMvc.perform(get("/api/citas/cliente/{id}", idCliente))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idCliente", is(1)))
                .andExpect(jsonPath("$[1].idCliente", is(1)));

        verify(citaService, times(1)).obtenerPorCliente(idCliente);
    }

    @Test
    @DisplayName("GET /api/citas/disponibilidad/dia - Debe obtener disponibilidad del día")
    void testGetDisponibilidadDia() throws Exception {
        // Given
        LocalDate fecha = LocalDate.now().plusDays(1);
        DisponibilidadDTO franja1 = new DisponibilidadDTO(fecha, LocalTime.of(9, 0), true);
        DisponibilidadDTO franja2 = new DisponibilidadDTO(fecha, LocalTime.of(9, 30), false);
        franja2.setIdCita(1L);
        franja2.setClienteNombre("Juan Pérez");

        List<DisponibilidadDTO> franjas = Arrays.asList(franja1, franja2);

        // Nota: Este endpoint probablemente llama a DisponibilidadService, no CitaService
        // Pero como estamos testeando el controlador, usamos el mock de CitaService
when(disponibilidadService.obtenerDisponibilidadDia(fecha)).thenReturn(franjas);

        // When & Then
        mockMvc.perform(get("/api/citas/disponibilidad/dia")
                .param("fecha", fecha.toString()))
                .andExpect(status().isOk());

verify(disponibilidadService, atLeastOnce()).obtenerDisponibilidadDia(fecha);
    }

    @Test
    @DisplayName("POST /api/citas/reservar - Debe reservar una cita con validación de disponibilidad")
    void testReservarCita() throws Exception {
        // Given
        CitaDTO nuevaReserva = new CitaDTO();
        nuevaReserva.setIdCliente(1L);
        nuevaReserva.setIdServicio(1L);
        nuevaReserva.setFecha(LocalDate.now().plusDays(5));
        nuevaReserva.setHora(LocalTime.of(14, 0));

        CitaDTO citaReservada = new CitaDTO();
        citaReservada.setIdCita(4L);
        citaReservada.setIdCliente(1L);
        citaReservada.setIdServicio(1L);
        citaReservada.setFecha(LocalDate.now().plusDays(5));
        citaReservada.setHora(LocalTime.of(14, 0));
        citaReservada.setEstado(EstadoCita.CONFIRMADA);

        when(disponibilidadService.verificarDisponibilidad(
                nuevaReserva.getFecha(),
                nuevaReserva.getHora()
        )).thenReturn(true);

        when(citaService.crear(any(CitaDTO.class))).thenReturn(citaReservada);

        // When & Then
        mockMvc.perform(post("/api/citas/reservar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaReserva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCita", is(4)))
                .andExpect(jsonPath("$.estado", is("CONFIRMADA")));

        verify(citaService, times(1)).crear(any(CitaDTO.class));
    }

    @Test
    @DisplayName("PUT /api/citas/{id}/cancelar - Debe cancelar una cita")
    void testCancelarCita() throws Exception {
        // Given
        Long idCita = 1L;
        CitaDTO citaCancelada = new CitaDTO();
        citaCancelada.setIdCita(idCita);
        citaCancelada.setIdCita(idCita);
        citaCancelada.setIdCliente(1L);
        citaCancelada.setIdServicio(1L);
        citaCancelada.setFecha(LocalDate.now().plusDays(1));
        citaCancelada.setHora(LocalTime.of(10, 0));
        citaCancelada.setEstado(EstadoCita.CANCELADA);

        when(citaService.obtenerPorId(idCita)).thenReturn(citaCancelada);

        com.peluqueria.gestioncitas.entity.Cita entidad = new com.peluqueria.gestioncitas.entity.Cita();
        entidad.setIdCita(idCita);
        entidad.setEstado(com.peluqueria.gestioncitas.entity.Cita.EstadoCita.CONFIRMADA);

        when(citaRepository.findById(idCita)).thenReturn(java.util.Optional.of(entidad));
        when(citaRepository.save(any(com.peluqueria.gestioncitas.entity.Cita.class))).thenReturn(entidad);

        mockMvc.perform(put("/api/citas/{id}/cancelar", idCita))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADA")));

verify(citaService, times(2)).obtenerPorId(idCita);
        verify(citaRepository, times(1)).findById(idCita);
        verify(citaRepository, times(1)).save(any(com.peluqueria.gestioncitas.entity.Cita.class));

    }
}