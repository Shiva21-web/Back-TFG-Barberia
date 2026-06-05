package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.ClienteDTO;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ClienteService
 * Utiliza Mockito para simular las dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente1;
    private Cliente cliente2;
    private ClienteDTO clienteDTO1;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        cliente1 = new Cliente();
        cliente1.setIdCliente(1L);
        cliente1.setNombre("Juan");
        cliente1.setApellidos("Pérez García");
        cliente1.setTelefono("612345678");
        cliente1.setEmail("juan@example.com");
        cliente1.setFechaRegistro(LocalDateTime.now());

        cliente2 = new Cliente();
        cliente2.setIdCliente(2L);
        cliente2.setNombre("María");
        cliente2.setApellidos("López Martínez");
        cliente2.setTelefono("623456789");
        cliente2.setEmail("maria@example.com");
        cliente2.setFechaRegistro(LocalDateTime.now());

        clienteDTO1 = new ClienteDTO();
        clienteDTO1.setIdCliente(1L);
        clienteDTO1.setNombre("Juan");
        clienteDTO1.setApellidos("Pérez García");
        clienteDTO1.setTelefono("612345678");
        clienteDTO1.setEmail("juan@example.com");
    }

    @Test
    @DisplayName("Debe obtener todos los clientes exitosamente")
    void testObtenerTodosLosClientes() {
        // Given
        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);
        when(clienteRepository.findAll()).thenReturn(clientes);
        when(modelMapper.map(any(Cliente.class), eq(ClienteDTO.class)))
                .thenReturn(clienteDTO1);

        // When
        List<ClienteDTO> resultado = clienteService.obtenerTodos();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        verify(clienteRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Cliente.class), eq(ClienteDTO.class));
    }

    @Test
    @DisplayName("Debe obtener un cliente por ID exitosamente")
    void testObtenerClientePorId() {
        // Given
        Long idCliente = 1L;
        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(cliente1));
        when(modelMapper.map(cliente1, ClienteDTO.class)).thenReturn(clienteDTO1);

        // When
        ClienteDTO resultado = clienteService.obtenerPorId(idCliente);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCliente()).isEqualTo(idCliente);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        verify(clienteRepository, times(1)).findById(idCliente);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void testObtenerClientePorIdNoExiste() {
        // Given
        Long idCliente = 999L;
        when(clienteRepository.findById(idCliente)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.obtenerPorId(idCliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado con ID: 999");
        verify(clienteRepository, times(1)).findById(idCliente);
    }

    @Test
    @DisplayName("Debe crear un cliente exitosamente")
    void testCrearCliente() {
        // Given
        ClienteDTO nuevoClienteDTO = new ClienteDTO();
        nuevoClienteDTO.setNombre("Pedro");
        nuevoClienteDTO.setApellidos("González");
        nuevoClienteDTO.setTelefono("634567890");
        nuevoClienteDTO.setEmail("pedro@example.com");

        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre("Pedro");
        nuevoCliente.setApellidos("González");
        nuevoCliente.setTelefono("634567890");
        nuevoCliente.setEmail("pedro@example.com");

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setIdCliente(3L);
        clienteGuardado.setNombre("Pedro");
        clienteGuardado.setApellidos("González");
        clienteGuardado.setTelefono("634567890");
        clienteGuardado.setEmail("pedro@example.com");

        when(clienteRepository.existsByEmail(nuevoClienteDTO.getEmail())).thenReturn(false);
        when(clienteRepository.existsByTelefono(nuevoClienteDTO.getTelefono())).thenReturn(false);
        when(modelMapper.map(nuevoClienteDTO, Cliente.class)).thenReturn(nuevoCliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);
        when(modelMapper.map(clienteGuardado, ClienteDTO.class)).thenReturn(nuevoClienteDTO);

        // When
        ClienteDTO resultado = clienteService.crear(nuevoClienteDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(clienteRepository).existsByEmail(nuevoClienteDTO.getEmail());
        verify(clienteRepository).existsByTelefono(nuevoClienteDTO.getTelefono());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cliente con email duplicado")
    void testCrearClienteConEmailDuplicado() {
        // Given
        ClienteDTO nuevoClienteDTO = new ClienteDTO();
        nuevoClienteDTO.setEmail("juan@example.com");
        nuevoClienteDTO.setTelefono("645678901");

        when(clienteRepository.existsByEmail(nuevoClienteDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clienteService.crear(nuevoClienteDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con este email");
        
        verify(clienteRepository).existsByEmail(nuevoClienteDTO.getEmail());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear cliente con teléfono duplicado")
    void testCrearClienteConTelefonoDuplicado() {
        // Given
        ClienteDTO nuevoClienteDTO = new ClienteDTO();
        nuevoClienteDTO.setEmail("nuevo@example.com");
        nuevoClienteDTO.setTelefono("612345678");

        when(clienteRepository.existsByEmail(nuevoClienteDTO.getEmail())).thenReturn(false);
        when(clienteRepository.existsByTelefono(nuevoClienteDTO.getTelefono())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clienteService.crear(nuevoClienteDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un cliente con este teléfono");
        
        verify(clienteRepository).existsByEmail(nuevoClienteDTO.getEmail());
        verify(clienteRepository).existsByTelefono(nuevoClienteDTO.getTelefono());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe actualizar un cliente exitosamente")
    void testActualizarCliente() {
        // Given
        Long idCliente = 1L;
        ClienteDTO clienteActualizadoDTO = new ClienteDTO();
        clienteActualizadoDTO.setNombre("Juan Carlos");
        clienteActualizadoDTO.setApellidos("Pérez García");
        clienteActualizadoDTO.setTelefono("612345678");
        clienteActualizadoDTO.setEmail("juancarlos@example.com");

        when(clienteRepository.findById(idCliente)).thenReturn(Optional.of(cliente1));
        when(clienteRepository.existsByEmail(clienteActualizadoDTO.getEmail())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente1);
        when(modelMapper.map(any(Cliente.class), eq(ClienteDTO.class))).thenReturn(clienteActualizadoDTO);

        // When
        ClienteDTO resultado = clienteService.actualizar(idCliente, clienteActualizadoDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(clienteRepository).findById(idCliente);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe eliminar un cliente exitosamente")
    void testEliminarCliente() {
        // Given
        Long idCliente = 1L;
        when(clienteRepository.existsById(idCliente)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(idCliente);

        // When
        clienteService.eliminar(idCliente);

        // Then
        verify(clienteRepository).existsById(idCliente);
        verify(clienteRepository).deleteById(idCliente);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cliente inexistente")
    void testEliminarClienteInexistente() {
        // Given
        Long idCliente = 999L;
        when(clienteRepository.existsById(idCliente)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clienteService.eliminar(idCliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente no encontrado con ID: 999");
        
        verify(clienteRepository).existsById(idCliente);
        verify(clienteRepository, never()).deleteById(idCliente);
    }

    @Test
    @DisplayName("Debe buscar clientes por término")
    void testBuscarClientes() {
        // Given
        String termino = "Juan";
        List<Cliente> clientesEncontrados = Arrays.asList(cliente1);
        when(clienteRepository.searchByNombreOrApellidos(termino)).thenReturn(clientesEncontrados);
        when(modelMapper.map(any(Cliente.class), eq(ClienteDTO.class))).thenReturn(clienteDTO1);

        // When
        List<ClienteDTO> resultado = clienteService.buscar(termino);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        verify(clienteRepository).searchByNombreOrApellidos(termino);
    }
}