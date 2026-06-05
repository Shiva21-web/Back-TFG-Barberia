package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.ServicioDTO;
import com.peluqueria.gestioncitas.entity.Servicio;
import com.peluqueria.gestioncitas.repository.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ServicioDTO> obtenerTodos() {
        return servicioRepository.findAll().stream()
                .map(servicio -> modelMapper.map(servicio, ServicioDTO.class))
                .collect(Collectors.toList());
    }

    public ServicioDTO obtenerPorId(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
        return modelMapper.map(servicio, ServicioDTO.class);
    }

    public ServicioDTO crear(ServicioDTO servicioDTO) {
        if (servicioRepository.existsByNombre(servicioDTO.getNombre())) {
            throw new RuntimeException("Ya existe un servicio con este nombre");
        }
        Servicio servicio = modelMapper.map(servicioDTO, Servicio.class);
        servicio = servicioRepository.save(servicio);
        return modelMapper.map(servicio, ServicioDTO.class);
    }

    public ServicioDTO actualizar(Long id, ServicioDTO servicioDTO) {
        Servicio servicioExistente = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        if (!servicioExistente.getNombre().equals(servicioDTO.getNombre()) &&
            servicioRepository.existsByNombre(servicioDTO.getNombre())) {
            throw new RuntimeException("Ya existe un servicio con este nombre");
        }

        servicioExistente.setNombre(servicioDTO.getNombre());
        servicioExistente.setDescripcion(servicioDTO.getDescripcion());
        servicioExistente.setDuracionMinutos(servicioDTO.getDuracionMinutos());
        servicioExistente.setPrecio(servicioDTO.getPrecio());

        servicioExistente = servicioRepository.save(servicioExistente);
        return modelMapper.map(servicioExistente, ServicioDTO.class);
    }

    public void eliminar(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    public List<ServicioDTO> buscar(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(servicio -> modelMapper.map(servicio, ServicioDTO.class))
                .collect(Collectors.toList());
    }
}