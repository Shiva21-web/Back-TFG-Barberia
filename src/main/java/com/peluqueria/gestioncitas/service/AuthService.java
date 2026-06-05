package com.peluqueria.gestioncitas.service;

import com.peluqueria.gestioncitas.dto.LoginRequestDTO;
import com.peluqueria.gestioncitas.dto.LoginResponseDTO;
import com.peluqueria.gestioncitas.dto.RegistroClienteDTO;
import com.peluqueria.gestioncitas.entity.Cliente;
import com.peluqueria.gestioncitas.entity.UsuarioAdmin;
import com.peluqueria.gestioncitas.exception.ResourceNotFoundException;
import com.peluqueria.gestioncitas.repository.ClienteRepository;
import com.peluqueria.gestioncitas.repository.UsuarioAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para manejar la autenticación de usuarios
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private UsuarioAdminRepository adminRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Login de administrador
     * @param loginRequest Credenciales de login
     * @return LoginResponseDTO con información del admin
     * @throws ResourceNotFoundException si las credenciales son incorrectas
     */
    public LoginResponseDTO loginAdmin(LoginRequestDTO loginRequest) {
        UsuarioAdmin admin = adminRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Usuario o contraseña incorrectos"));

        // Verificar contraseña (en texto plano)
        if (!admin.getContrasena().equals(loginRequest.getContrasena())) {
            throw new ResourceNotFoundException("Usuario o contraseña incorrectos");
        }

        // Verificar que el admin esté activo
        if (!admin.getActivo()) {
            throw new ResourceNotFoundException("Usuario inactivo");
        }

        return new LoginResponseDTO(
            admin.getIdAdmin(),
            admin.getNombre(),
            admin.getEmail(),
            "ADMIN",
            "Login exitoso"
        );
    }

    /**
     * Login de cliente
     * @param loginRequest Credenciales de login (nombre y contraseña)
     * @return LoginResponseDTO con información del cliente
     * @throws ResourceNotFoundException si las credenciales son incorrectas
     */
    public LoginResponseDTO loginCliente(LoginRequestDTO loginRequest) {
        Cliente cliente = clienteRepository.findByNombreAndContrasena(
                loginRequest.getUsername(), 
                loginRequest.getContrasena())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Nombre o contraseña incorrectos"));

        return new LoginResponseDTO(
            cliente.getIdCliente(),
            cliente.getNombre(),
            cliente.getApellidos(),
            cliente.getEmail(),
            "CLIENTE",
            "Login exitoso"
        );
    }

    /**
     * Registro de nuevo cliente
     * @param registroDTO Datos del nuevo cliente
     * @return LoginResponseDTO con información del cliente registrado
     * @throws IllegalArgumentException si el email o teléfono ya existen
     */
    public LoginResponseDTO registrarCliente(RegistroClienteDTO registroDTO) {
        // Verificar si el email ya existe
        if (clienteRepository.existsByEmail(registroDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Verificar si el teléfono ya existe
        if (clienteRepository.existsByTelefono(registroDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        // Crear nuevo cliente
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setNombre(registroDTO.getNombre());
        nuevoCliente.setApellidos(registroDTO.getApellidos());
        nuevoCliente.setTelefono(registroDTO.getTelefono());
        nuevoCliente.setEmail(registroDTO.getEmail());
        nuevoCliente.setContrasena(registroDTO.getContrasena()); // En texto plano

        // Guardar en la base de datos
        Cliente clienteGuardado = clienteRepository.save(nuevoCliente);

        return new LoginResponseDTO(
            clienteGuardado.getIdCliente(),
            clienteGuardado.getNombre(),
            clienteGuardado.getApellidos(),
            clienteGuardado.getEmail(),
            "CLIENTE",
            "Registro exitoso. Bienvenido!"
        );
    }
}