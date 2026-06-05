package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.ServicioDTO;
import com.peluqueria.gestioncitas.entity.Servicio;
import com.peluqueria.gestioncitas.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ServicioService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de ServicioService")
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio servicio1;
    private Servicio servicio2;
    private ServicioDTO servicioDTO1;

    @BeforeEach
    void setUp() {
        servicio1 = new Servicio();
        servicio1.setIdServicio(1L);
        servicio1.setNombre("Corte de cabello");
        servicio1.setDescripcion("Corte básico");
        servicio1.setDuracionMinutos(30);
        servicio1.setPrecio(new BigDecimal("15.00"));

        servicio2 = new Servicio();
        servicio2.setIdServicio(2L);
        servicio2.setNombre("Tinte completo");
        servicio2.setDescripcion("Tinte de todo el cabello");
        servicio2.setDuracionMinutos(90);
        servicio2.setPrecio(new BigDecimal("45.00"));

        servicioDTO1 = new ServicioDTO();
        servicioDTO1.setIdServicio(1L);
        servicioDTO1.setNombre("Corte de cabello");
        servicioDTO1.setDescripcion("Corte básico");
        servicioDTO1.setDuracionMinutos(30);
        servicioDTO1.setPrecio(new BigDecimal("15.00"));
    }

    @Test
    @DisplayName("Debe obtener todos los servicios exitosamente")
    void testObtenerTodosLosServicios() {
        // Given
        List<Servicio> servicios = Arrays.asList(servicio1, servicio2);
        when(servicioRepository.findAll()).thenReturn(servicios);
        when(modelMapper.map(any(Servicio.class), eq(ServicioDTO.class)))
                .thenReturn(servicioDTO1);

        // When
        List<ServicioDTO> resultado = servicioService.obtenerTodos();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        verify(servicioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un servicio por ID exitosamente")
    void testObtenerServicioPorId() {
        // Given
        Long idServicio = 1L;
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio1));
        when(modelMapper.map(servicio1, ServicioDTO.class)).thenReturn(servicioDTO1);

        // When
        ServicioDTO resultado = servicioService.obtenerPorId(idServicio);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdServicio()).isEqualTo(idServicio);
        assertThat(resultado.getNombre()).isEqualTo("Corte de cabello");
        verify(servicioRepository, times(1)).findById(idServicio);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el servicio no existe")
    void testObtenerServicioPorIdNoExiste() {
        // Given
        Long idServicio = 999L;
        when(servicioRepository.findById(idServicio)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> servicioService.obtenerPorId(idServicio))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio no encontrado");
        verify(servicioRepository, times(1)).findById(idServicio);
    }

    @Test
    @DisplayName("Debe crear un servicio exitosamente")
    void testCrearServicio() {
        // Given
        ServicioDTO nuevoServicioDTO = new ServicioDTO();
        nuevoServicioDTO.setNombre("Mechas");
        nuevoServicioDTO.setDescripcion("Mechas californianas");
        nuevoServicioDTO.setDuracionMinutos(120);
        nuevoServicioDTO.setPrecio(new BigDecimal("55.00"));

        Servicio nuevoServicio = new Servicio();
        nuevoServicio.setNombre("Mechas");

        Servicio servicioGuardado = new Servicio();
        servicioGuardado.setIdServicio(3L);
        servicioGuardado.setNombre("Mechas");

        when(servicioRepository.existsByNombre(nuevoServicioDTO.getNombre())).thenReturn(false);
        when(modelMapper.map(nuevoServicioDTO, Servicio.class)).thenReturn(nuevoServicio);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicioGuardado);
        when(modelMapper.map(servicioGuardado, ServicioDTO.class)).thenReturn(nuevoServicioDTO);

        // When
        ServicioDTO resultado = servicioService.crear(nuevoServicioDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(servicioRepository).existsByNombre(nuevoServicioDTO.getNombre());
        verify(servicioRepository).save(any(Servicio.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear servicio con nombre duplicado")
    void testCrearServicioConNombreDuplicado() {
        // Given
        ServicioDTO nuevoServicioDTO = new ServicioDTO();
        nuevoServicioDTO.setNombre("Corte de cabello");

        when(servicioRepository.existsByNombre(nuevoServicioDTO.getNombre())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> servicioService.crear(nuevoServicioDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un servicio con este nombre");
        
        verify(servicioRepository).existsByNombre(nuevoServicioDTO.getNombre());
        verify(servicioRepository, never()).save(any(Servicio.class));
    }

    @Test
    @DisplayName("Debe actualizar un servicio exitosamente")
    void testActualizarServicio() {
        // Given
        Long idServicio = 1L;
        ServicioDTO servicioActualizadoDTO = new ServicioDTO();
        servicioActualizadoDTO.setNombre("Corte premium");
        servicioActualizadoDTO.setDescripcion("Corte premium con estilista");
        servicioActualizadoDTO.setDuracionMinutos(45);
        servicioActualizadoDTO.setPrecio(new BigDecimal("25.00"));

        when(servicioRepository.findById(idServicio)).thenReturn(Optional.of(servicio1));
        when(servicioRepository.existsByNombre(servicioActualizadoDTO.getNombre())).thenReturn(false);
        when(servicioRepository.save(any(Servicio.class))).thenReturn(servicio1);
        when(modelMapper.map(any(Servicio.class), eq(ServicioDTO.class))).thenReturn(servicioActualizadoDTO);

        // When
        ServicioDTO resultado = servicioService.actualizar(idServicio, servicioActualizadoDTO);

        // Then
        assertThat(resultado).isNotNull();
        verify(servicioRepository).findById(idServicio);
        verify(servicioRepository).save(any(Servicio.class));
    }

    @Test
    @DisplayName("Debe eliminar un servicio exitosamente")
    void testEliminarServicio() {
        // Given
        Long idServicio = 1L;
        when(servicioRepository.existsById(idServicio)).thenReturn(true);
        doNothing().when(servicioRepository).deleteById(idServicio);

        // When
        servicioService.eliminar(idServicio);

        // Then
        verify(servicioRepository).existsById(idServicio);
        verify(servicioRepository).deleteById(idServicio);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar servicio inexistente")
    void testEliminarServicioInexistente() {
        // Given
        Long idServicio = 999L;
        when(servicioRepository.existsById(idServicio)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> servicioService.eliminar(idServicio))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio no encontrado");
        
        verify(servicioRepository).existsById(idServicio);
        verify(servicioRepository, never()).deleteById(idServicio);
    }
}