package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.LoginRequestDTO;
import com.peluqueria.gestioncitas.dto.LoginResponseDTO;
import com.peluqueria.gestioncitas.dto.RegistroClienteDTO;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.entity.UsuarioAdmin;
import com.peluqueria.gestioncitas.exception.ResourceNotFoundException;
import com.peluqueria.gestioncitas.repository.ClienteRepository;
import com.peluqueria.gestioncitas.repository.UsuarioAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioAdminRepository adminRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AuthService authService;

    private UsuarioAdmin adminActivo;
    private UsuarioAdmin adminInactivo;
    private Cliente cliente;
    private LoginRequestDTO loginRequestAdmin;
    private LoginRequestDTO loginRequestCliente;
    private RegistroClienteDTO registroDTO;

    @BeforeEach
    void setUp() {
        // Configurar admin activo
        adminActivo = new UsuarioAdmin();
        adminActivo.setIdAdmin(1L);
        adminActivo.setUsername("admin");
        adminActivo.setContrasena("admin");
        adminActivo.setNombre("Administrador");
        adminActivo.setEmail("admin@peluqueria.com");
        adminActivo.setActivo(true);

        // Configurar admin inactivo
        adminInactivo = new UsuarioAdmin();
        adminInactivo.setIdAdmin(2L);
        adminInactivo.setUsername("admin_inactivo");
        adminInactivo.setContrasena("password");
        adminInactivo.setNombre("Admin Inactivo");
        adminInactivo.setEmail("inactivo@peluqueria.com");
        adminInactivo.setActivo(false);

        // Configurar cliente
        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan");
        cliente.setApellidos("Pérez García");
        cliente.setTelefono("612345678");
        cliente.setEmail("juan@example.com");
        cliente.setContrasena("1234");

        // Configurar LoginRequestDTO para admin
        loginRequestAdmin = new LoginRequestDTO();
        loginRequestAdmin.setUsername("admin");
        loginRequestAdmin.setContrasena("admin");

        // Configurar LoginRequestDTO para cliente
        loginRequestCliente = new LoginRequestDTO();
        loginRequestCliente.setUsername("Juan");
        loginRequestCliente.setContrasena("1234");

        // Configurar RegistroClienteDTO
        registroDTO = new RegistroClienteDTO();
        registroDTO.setNombre("Pedro");
        registroDTO.setApellidos("González López");
        registroDTO.setTelefono("634567890");
        registroDTO.setEmail("pedro@example.com");
        registroDTO.setContrasena("password123");
    }

    @Test
    @DisplayName("Debe hacer login de admin exitosamente")
    void testLoginAdminExitoso() {
        // Given
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(adminActivo));

        // When
        LoginResponseDTO response = authService.loginAdmin(loginRequestAdmin);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Administrador");
        assertThat(response.getEmail()).isEqualTo("admin@peluqueria.com");
        assertThat(response.getTipoUsuario()).isEqualTo("ADMIN");
        assertThat(response.getMensaje()).isEqualTo("Login exitoso");
        
        verify(adminRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el admin no existe")
    void testLoginAdminUsuarioNoExiste() {
        // Given
        when(adminRepository.findByUsername("admin_inexistente")).thenReturn(Optional.empty());
        
        LoginRequestDTO requestInvalido = new LoginRequestDTO();
        requestInvalido.setUsername("admin_inexistente");
        requestInvalido.setContrasena("cualquiera");

        // When & Then
        assertThatThrownBy(() -> authService.loginAdmin(requestInvalido))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario o contraseña incorrectos");
        
        verify(adminRepository, times(1)).findByUsername("admin_inexistente");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la contraseña del admin es incorrecta")
    void testLoginAdminContrasenaIncorrecta() {
        // Given
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(adminActivo));
        
        LoginRequestDTO requestInvalido = new LoginRequestDTO();
        requestInvalido.setUsername("admin");
        requestInvalido.setContrasena("contraseña_incorrecta");

        // When & Then
        assertThatThrownBy(() -> authService.loginAdmin(requestInvalido))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario o contraseña incorrectos");
        
        verify(adminRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el admin está inactivo")
    void testLoginAdminInactivo() {
        // Given
        when(adminRepository.findByUsername("admin_inactivo")).thenReturn(Optional.of(adminInactivo));
        
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("admin_inactivo");
        request.setContrasena("password");

        // When & Then
        assertThatThrownBy(() -> authService.loginAdmin(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuario inactivo");
        
        verify(adminRepository, times(1)).findByUsername("admin_inactivo");
    }

    @Test
    @DisplayName("Debe hacer login de cliente exitosamente")
    void testLoginClienteExitoso() {
        // Given
        when(clienteRepository.findByNombreAndContrasena("Juan", "1234"))
                .thenReturn(Optional.of(cliente));

        // When
        LoginResponseDTO response = authService.loginCliente(loginRequestCliente);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Juan");
        assertThat(response.getApellidos()).isEqualTo("Pérez García");
        assertThat(response.getEmail()).isEqualTo("juan@example.com");
        assertThat(response.getTipoUsuario()).isEqualTo("CLIENTE");
        assertThat(response.getMensaje()).isEqualTo("Login exitoso");
        
        verify(clienteRepository, times(1)).findByNombreAndContrasena("Juan", "1234");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando las credenciales del cliente son incorrectas")
    void testLoginClienteCredencialesIncorrectas() {
        // Given
        when(clienteRepository.findByNombreAndContrasena("Juan", "contraseña_incorrecta"))
                .thenReturn(Optional.empty());
        
        LoginRequestDTO requestInvalido = new LoginRequestDTO();
        requestInvalido.setUsername("Juan");
        requestInvalido.setContrasena("contraseña_incorrecta");

        // When & Then
        assertThatThrownBy(() -> authService.loginCliente(requestInvalido))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Nombre o contraseña incorrectos");
        
        verify(clienteRepository, times(1)).findByNombreAndContrasena("Juan", "contraseña_incorrecta");
    }

    @Test
    @DisplayName("Debe registrar un nuevo cliente exitosamente")
    void testRegistrarClienteExitoso() {
        // Given
        when(clienteRepository.existsByEmail(registroDTO.getEmail())).thenReturn(false);
        when(clienteRepository.existsByTelefono(registroDTO.getTelefono())).thenReturn(false);
        
        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setIdCliente(3L);
        clienteGuardado.setNombre(registroDTO.getNombre());
        clienteGuardado.setApellidos(registroDTO.getApellidos());
        clienteGuardado.setTelefono(registroDTO.getTelefono());
        clienteGuardado.setEmail(registroDTO.getEmail());
        clienteGuardado.setContrasena(registroDTO.getContrasena());
        
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

        // When
        LoginResponseDTO response = authService.registrarCliente(registroDTO);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getNombre()).isEqualTo("Pedro");
        assertThat(response.getApellidos()).isEqualTo("González López");
        assertThat(response.getEmail()).isEqualTo("pedro@example.com");
        assertThat(response.getTipoUsuario()).isEqualTo("CLIENTE");
        assertThat(response.getMensaje()).isEqualTo("Registro exitoso. Bienvenido!");
        
        verify(clienteRepository).existsByEmail(registroDTO.getEmail());
        verify(clienteRepository).existsByTelefono(registroDTO.getTelefono());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al registrar cliente con email duplicado")
    void testRegistrarClienteEmailDuplicado() {
        // Given
        when(clienteRepository.existsByEmail(registroDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.registrarCliente(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El email ya está registrado");
        
        verify(clienteRepository).existsByEmail(registroDTO.getEmail());
        verify(clienteRepository, never()).existsByTelefono(any());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al registrar cliente con teléfono duplicado")
    void testRegistrarClienteTelefonoDuplicado() {
        // Given
        when(clienteRepository.existsByEmail(registroDTO.getEmail())).thenReturn(false);
        when(clienteRepository.existsByTelefono(registroDTO.getTelefono())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.registrarCliente(registroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El teléfono ya está registrado");
        
        verify(clienteRepository).existsByEmail(registroDTO.getEmail());
        verify(clienteRepository).existsByTelefono(registroDTO.getTelefono());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }
}