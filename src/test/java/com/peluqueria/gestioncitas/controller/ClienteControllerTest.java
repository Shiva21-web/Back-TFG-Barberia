package com.peluqueria.gestioncitas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peluqueria.gestioncitas.dto.ClienteDTO;
import com.peluqueria.gestioncitas.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para ClienteController usando MockMVC
 */
@WebMvcTest(ClienteController.class)
@DisplayName("Tests de ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    private ClienteDTO clienteDTO1;
    private ClienteDTO clienteDTO2;

    @BeforeEach
    void setUp() {
        clienteDTO1 = new ClienteDTO();
        clienteDTO1.setIdCliente(1L);
        clienteDTO1.setNombre("Juan");
        clienteDTO1.setApellidos("Pérez García");
        clienteDTO1.setTelefono("612345678");
        clienteDTO1.setEmail("juan@example.com");
        clienteDTO1.setFechaRegistro(LocalDateTime.now());

        clienteDTO2 = new ClienteDTO();
        clienteDTO2.setIdCliente(2L);
        clienteDTO2.setNombre("María");
        clienteDTO2.setApellidos("López Martínez");
        clienteDTO2.setTelefono("623456789");
        clienteDTO2.setEmail("maria@example.com");
        clienteDTO2.setFechaRegistro(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/clientes - Debe obtener todos los clientes")
    void testGetTodosLosClientes() throws Exception {
        // Given
        List<ClienteDTO> clientes = Arrays.asList(clienteDTO1, clienteDTO2);
        when(clienteService.obtenerTodos()).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")))
                .andExpect(jsonPath("$[1].nombre", is("María")));

        verify(clienteService, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Debe obtener un cliente por ID")
    void testGetClientePorId() throws Exception {
        // Given
        Long idCliente = 1L;
        when(clienteService.obtenerPorId(idCliente)).thenReturn(clienteDTO1);

        // When & Then
        mockMvc.perform(get("/api/clientes/{id}", idCliente))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idCliente", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.email", is("juan@example.com")));

        verify(clienteService, times(1)).obtenerPorId(idCliente);
    }

    @Test
    @DisplayName("POST /api/clientes - Debe crear un cliente exitosamente")
    void testCrearCliente() throws Exception {
        // Given
        ClienteDTO nuevoCliente = new ClienteDTO();
        nuevoCliente.setNombre("Pedro");
        nuevoCliente.setApellidos("González");
        nuevoCliente.setTelefono("634567890");
        nuevoCliente.setEmail("pedro@example.com");

        ClienteDTO clienteCreado = new ClienteDTO();
        clienteCreado.setIdCliente(3L);
        clienteCreado.setNombre("Pedro");
        clienteCreado.setApellidos("González");
        clienteCreado.setTelefono("634567890");
        clienteCreado.setEmail("pedro@example.com");

        when(clienteService.crear(any(ClienteDTO.class))).thenReturn(clienteCreado);

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoCliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente", is(3)))
                .andExpect(jsonPath("$.nombre", is("Pedro")));

        verify(clienteService, times(1)).crear(any(ClienteDTO.class));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Debe actualizar un cliente")
    void testActualizarCliente() throws Exception {
        // Given
        Long idCliente = 1L;
        ClienteDTO clienteActualizado = new ClienteDTO();
        clienteActualizado.setNombre("Juan Carlos");
        clienteActualizado.setApellidos("Pérez García");
        clienteActualizado.setTelefono("612345678");
        clienteActualizado.setEmail("juancarlos@example.com");

        when(clienteService.actualizar(eq(idCliente), any(ClienteDTO.class)))
                .thenReturn(clienteActualizado);

        // When & Then
        mockMvc.perform(put("/api/clientes/{id}", idCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Juan Carlos")));

        verify(clienteService, times(1)).actualizar(eq(idCliente), any(ClienteDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/clientes/{id} - Debe eliminar un cliente")
    void testEliminarCliente() throws Exception {
        // Given
        Long idCliente = 1L;
        doNothing().when(clienteService).eliminar(idCliente);

        // When & Then
        mockMvc.perform(delete("/api/clientes/{id}", idCliente))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).eliminar(idCliente);
    }

    @Test
    @DisplayName("GET /api/clientes/buscar - Debe buscar clientes por término")
    void testBuscarClientes() throws Exception {
        // Given
        String termino = "Juan";
        List<ClienteDTO> resultados = Arrays.asList(clienteDTO1);
        when(clienteService.buscar(termino)).thenReturn(resultados);

        // When & Then
        mockMvc.perform(get("/api/clientes/buscar")
                        .param("termino", termino))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")));

        verify(clienteService, times(1)).buscar(termino);
    }
}