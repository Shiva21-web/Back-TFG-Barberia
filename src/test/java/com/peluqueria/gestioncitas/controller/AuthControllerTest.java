package com.peluqueria.gestioncitas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peluqueria.gestioncitas.dto.LoginRequestDTO;
import com.peluqueria.gestioncitas.dto.LoginResponseDTO;
import com.peluqueria.gestioncitas.dto.RegistroClienteDTO;
import com.peluqueria.gestioncitas.exception.ResourceNotFoundException;
import com.peluqueria.gestioncitas.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController usando MockMVC
 */
@WebMvcTest(AuthController.class)
@DisplayName("Tests de AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private LoginRequestDTO loginRequestAdmin;
    private LoginRequestDTO loginRequestCliente;
    private RegistroClienteDTO registroClienteDTO;
    private LoginResponseDTO loginResponseAdmin;
    private LoginResponseDTO loginResponseCliente;

    @BeforeEach
    void setUp() {
        // Configurar LoginRequestDTO para admin
        loginRequestAdmin = new LoginRequestDTO();
        loginRequestAdmin.setUsername("admin");
        loginRequestAdmin.setContrasena("admin");

        // Configurar LoginRequestDTO para cliente
        loginRequestCliente = new LoginRequestDTO();
        loginRequestCliente.setUsername("Juan");
        loginRequestCliente.setContrasena("1234");

        // Configurar RegistroClienteDTO
        registroClienteDTO = new RegistroClienteDTO();
        registroClienteDTO.setNombre("Pedro");
        registroClienteDTO.setApellidos("González López");
        registroClienteDTO.setTelefono("634567890");
        registroClienteDTO.setEmail("pedro@example.com");
        registroClienteDTO.setContrasena("password123");

        // Configurar LoginResponseDTO para admin
        loginResponseAdmin = new LoginResponseDTO(
            1L,
            "Administrador",
            "admin@peluqueria.com",
            "ADMIN",
            "Login exitoso"
        );

        // Configurar LoginResponseDTO para cliente
        loginResponseCliente = new LoginResponseDTO(
            1L,
            "Juan",
            "Pérez García",
            "juan@example.com",
            "CLIENTE",
            "Login exitoso"
        );
    }

    @Test
    @DisplayName("POST /api/auth/login/admin - Debe hacer login de admin exitosamente")
    void testLoginAdminExitoso() throws Exception {
        // Given
        when(authService.loginAdmin(any(LoginRequestDTO.class))).thenReturn(loginResponseAdmin);

        // When & Then
        mockMvc.perform(post("/api/auth/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestAdmin)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Administrador")))
                .andExpect(jsonPath("$.email", is("admin@peluqueria.com")))
                .andExpect(jsonPath("$.tipoUsuario", is("ADMIN")))
                .andExpect(jsonPath("$.mensaje", is("Login exitoso")));

        verify(authService, times(1)).loginAdmin(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login/admin - Debe retornar 401 con credenciales incorrectas")
    void testLoginAdminCredencialesIncorrectas() throws Exception {
        // Given
        when(authService.loginAdmin(any(LoginRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Usuario o contraseña incorrectos"));

        LoginRequestDTO requestInvalido = new LoginRequestDTO();
        requestInvalido.setUsername("admin");
        requestInvalido.setContrasena("incorrecta");

        // When & Then
        mockMvc.perform(post("/api/auth/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje", containsString("Usuario o contraseña incorrectos")));

        verify(authService, times(1)).loginAdmin(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login/cliente - Debe hacer login de cliente exitosamente")
    void testLoginClienteExitoso() throws Exception {
        // Given
        when(authService.loginCliente(any(LoginRequestDTO.class))).thenReturn(loginResponseCliente);

        // When & Then
        mockMvc.perform(post("/api/auth/login/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestCliente)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.apellidos", is("Pérez García")))
                .andExpect(jsonPath("$.email", is("juan@example.com")))
                .andExpect(jsonPath("$.tipoUsuario", is("CLIENTE")))
                .andExpect(jsonPath("$.mensaje", is("Login exitoso")));

        verify(authService, times(1)).loginCliente(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/login/cliente - Debe retornar 401 con credenciales incorrectas")
    void testLoginClienteCredencialesIncorrectas() throws Exception {
        // Given
        when(authService.loginCliente(any(LoginRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Nombre o contraseña incorrectos"));

        LoginRequestDTO requestInvalido = new LoginRequestDTO();
        requestInvalido.setUsername("Juan");
        requestInvalido.setContrasena("incorrecta");

        // When & Then
        mockMvc.perform(post("/api/auth/login/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje", containsString("Nombre o contraseña incorrectos")));

        verify(authService, times(1)).loginCliente(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/registro/cliente - Debe registrar cliente exitosamente")
    void testRegistroClienteExitoso() throws Exception {
        // Given
        LoginResponseDTO responseRegistro = new LoginResponseDTO(
            3L,
            "Pedro",
            "González López",
            "pedro@example.com",
            "CLIENTE",
            "Registro exitoso. Bienvenido!"
        );
        
        when(authService.registrarCliente(any(RegistroClienteDTO.class))).thenReturn(responseRegistro);

        // When & Then
        mockMvc.perform(post("/api/auth/registro/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroClienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Pedro")))
                .andExpect(jsonPath("$.apellidos", is("González López")))
                .andExpect(jsonPath("$.email", is("pedro@example.com")))
                .andExpect(jsonPath("$.tipoUsuario", is("CLIENTE")))
                .andExpect(jsonPath("$.mensaje", containsString("Registro exitoso")));

        verify(authService, times(1)).registrarCliente(any(RegistroClienteDTO.class));
    }

    @Test
    @DisplayName("POST /api/auth/registro/cliente - Debe retornar 400 con email duplicado")
    void testRegistroClienteEmailDuplicado() throws Exception {
        // Given
        when(authService.registrarCliente(any(RegistroClienteDTO.class)))
                .thenThrow(new IllegalArgumentException("El email ya está registrado"));

        // When & Then
        mockMvc.perform(post("/api/auth/registro/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroClienteDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mensaje", containsString("El email ya está registrado")));

        verify(authService, times(1)).registrarCliente(any(RegistroClienteDTO.class));
    }
}