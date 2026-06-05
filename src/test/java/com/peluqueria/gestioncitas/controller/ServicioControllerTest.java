package com.peluqueria.gestioncitas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peluqueria.gestioncitas.dto.ServicioDTO;
import com.peluqueria.gestioncitas.service.ServicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para ServicioController usando MockMVC
 */
@WebMvcTest(ServicioController.class)
@DisplayName("Tests de ServicioController")
class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServicioService servicioService;

    private ServicioDTO servicioDTO1;
    private ServicioDTO servicioDTO2;

    @BeforeEach
    void setUp() {
        servicioDTO1 = new ServicioDTO();
        servicioDTO1.setIdServicio(1L);
        servicioDTO1.setNombre("Corte de cabello");
        servicioDTO1.setDescripcion("Corte básico");
        servicioDTO1.setDuracionMinutos(30);
        servicioDTO1.setPrecio(new BigDecimal("15.00"));

        servicioDTO2 = new ServicioDTO();
        servicioDTO2.setIdServicio(2L);
        servicioDTO2.setNombre("Tinte completo");
        servicioDTO2.setDescripcion("Tinte de todo el cabello");
        servicioDTO2.setDuracionMinutos(90);
        servicioDTO2.setPrecio(new BigDecimal("45.00"));
    }

    @Test
    @DisplayName("GET /api/servicios - Debe obtener todos los servicios")
    void testGetTodosLosServicios() throws Exception {
        // Given
        List<ServicioDTO> servicios = Arrays.asList(servicioDTO1, servicioDTO2);
        when(servicioService.obtenerTodos()).thenReturn(servicios);

        // When & Then
        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Corte de cabello")))
                .andExpect(jsonPath("$[1].nombre", is("Tinte completo")));

        verify(servicioService, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("GET /api/servicios/{id} - Debe obtener un servicio por ID")
    void testGetServicioPorId() throws Exception {
        // Given
        Long idServicio = 1L;
        when(servicioService.obtenerPorId(idServicio)).thenReturn(servicioDTO1);

        // When & Then
        mockMvc.perform(get("/api/servicios/{id}", idServicio))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idServicio", is(1)))
                .andExpect(jsonPath("$.nombre", is("Corte de cabello")))
                .andExpect(jsonPath("$.precio", is(15.00)));

        verify(servicioService, times(1)).obtenerPorId(idServicio);
    }

    @Test
    @DisplayName("POST /api/servicios - Debe crear un servicio exitosamente")
    void testCrearServicio() throws Exception {
        // Given
        ServicioDTO nuevoServicio = new ServicioDTO();
        nuevoServicio.setNombre("Mechas");
        nuevoServicio.setDescripcion("Mechas californianas");
        nuevoServicio.setDuracionMinutos(120);
        nuevoServicio.setPrecio(new BigDecimal("55.00"));

        ServicioDTO servicioCreado = new ServicioDTO();
        servicioCreado.setIdServicio(3L);
        servicioCreado.setNombre("Mechas");
        servicioCreado.setDescripcion("Mechas californianas");
        servicioCreado.setDuracionMinutos(120);
        servicioCreado.setPrecio(new BigDecimal("55.00"));

        when(servicioService.crear(any(ServicioDTO.class))).thenReturn(servicioCreado);

        // When & Then
        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoServicio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idServicio", is(3)))
                .andExpect(jsonPath("$.nombre", is("Mechas")));

        verify(servicioService, times(1)).crear(any(ServicioDTO.class));
    }

    @Test
    @DisplayName("PUT /api/servicios/{id} - Debe actualizar un servicio")
    void testActualizarServicio() throws Exception {
        // Given
        Long idServicio = 1L;
        ServicioDTO servicioActualizado = new ServicioDTO();
        servicioActualizado.setNombre("Corte premium");
        servicioActualizado.setDescripcion("Corte premium con estilista");
        servicioActualizado.setDuracionMinutos(45);
        servicioActualizado.setPrecio(new BigDecimal("25.00"));

        when(servicioService.actualizar(eq(idServicio), any(ServicioDTO.class)))
                .thenReturn(servicioActualizado);

        // When & Then
        mockMvc.perform(put("/api/servicios/{id}", idServicio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Corte premium")));

        verify(servicioService, times(1)).actualizar(eq(idServicio), any(ServicioDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/servicios/{id} - Debe eliminar un servicio")
    void testEliminarServicio() throws Exception {
        // Given
        Long idServicio = 1L;
        doNothing().when(servicioService).eliminar(idServicio);

        // When & Then
        mockMvc.perform(delete("/api/servicios/{id}", idServicio))
                .andExpect(status().isNoContent());

        verify(servicioService, times(1)).eliminar(idServicio);
    }

    @Test
    @DisplayName("GET /api/servicios/buscar - Debe buscar servicios por nombre")
    void testBuscarServicios() throws Exception {
        // Given
        String termino = "Corte";
        List<ServicioDTO> resultados = Arrays.asList(servicioDTO1);
        when(servicioService.buscar(termino)).thenReturn(resultados);

        // When & Then
        mockMvc.perform(get("/api/servicios/buscar")
                        .param("nombre", termino))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Corte de cabello")));

        verify(servicioService, times(1)).buscar(termino);
    }
}
