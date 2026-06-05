package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.ClienteDTO;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la lógica de negocio de Clientes
 * 
 * @Service: Indica que es un componente de servicio de Spring
 * @Transactional: Gestiona transacciones automáticamente
 */
@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Obtiene todos los clientes
     */
    public List<ClienteDTO> obtenerTodos() {
        return clienteRepository.findAll().stream()
                .map(cliente -> modelMapper.map(cliente, ClienteDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por ID
     */
    public ClienteDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    /**
     * Crea un nuevo cliente
     */
    public ClienteDTO crear(ClienteDTO clienteDTO) {
        // Validar que no exista el email
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con este email");
        }
        
        // Validar que no exista el teléfono
        if (clienteRepository.existsByTelefono(clienteDTO.getTelefono())) {
            throw new RuntimeException("Ya existe un cliente con este teléfono");
        }

        Cliente cliente = modelMapper.map(clienteDTO, Cliente.class);
        cliente = clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    /**
     * Actualiza un cliente existente
     */
    public ClienteDTO actualizar(Long id, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        // Validar email si cambió
        if (!clienteExistente.getEmail().equals(clienteDTO.getEmail()) &&
            clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con este email");
        }

        // Validar teléfono si cambió
        if (!clienteExistente.getTelefono().equals(clienteDTO.getTelefono()) &&
            clienteRepository.existsByTelefono(clienteDTO.getTelefono())) {
            throw new RuntimeException("Ya existe un cliente con este teléfono");
        }

        clienteExistente.setNombre(clienteDTO.getNombre());
        clienteExistente.setApellidos(clienteDTO.getApellidos());
        clienteExistente.setTelefono(clienteDTO.getTelefono());
        clienteExistente.setEmail(clienteDTO.getEmail());

        clienteExistente = clienteRepository.save(clienteExistente);
        return modelMapper.map(clienteExistente, ClienteDTO.class);
    }

    /**
     * Elimina un cliente
     */
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    /**
     * Busca clientes por término (nombre o apellidos)
     */
    public List<ClienteDTO> buscar(String termino) {
        return clienteRepository.searchByNombreOrApellidos(termino).stream()
                .map(cliente -> modelMapper.map(cliente, ClienteDTO.class))
                .collect(Collectors.toList());
    }
}
