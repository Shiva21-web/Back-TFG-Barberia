package com.peluqueria.gestioncitas.controller;

import com.peluqueria.gestioncitas.dto.LoginRequestDTO;
import com.peluqueria.gestioncitas.dto.LoginResponseDTO;
import com.peluqueria.gestioncitas.dto.RegistroClienteDTO;
import com.peluqueria.gestioncitas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar autenticación de usuarios
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Login para administrador
     * POST /api/auth/login/admin
     * 
     * @param loginRequest Credenciales (username y contraseña)
     * @return LoginResponseDTO con información del usuario
     */
    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponseDTO> loginAdmin(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.loginAdmin(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Login para cliente
     * POST /api/auth/login/cliente
     * 
     * @param loginRequest Credenciales (nombre y contraseña)
     * @return LoginResponseDTO con información del cliente
     */
    @PostMapping("/login/cliente")
    public ResponseEntity<LoginResponseDTO> loginCliente(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.loginCliente(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Registro de nuevo cliente
     * POST /api/auth/registro/cliente
     * 
     * @param registroDTO Datos del nuevo cliente
     * @return LoginResponseDTO con información del cliente registrado
     */
    @PostMapping("/registro/cliente")
    public ResponseEntity<LoginResponseDTO> registrarCliente(@Valid @RequestBody RegistroClienteDTO registroDTO) {
        try {
            LoginResponseDTO response = authService.registrarCliente(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            LoginResponseDTO errorResponse = new LoginResponseDTO();
            errorResponse.setMensaje("Error al registrar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint de prueba para verificar que el servidor está funcionando
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("El servidor de autenticación está funcionando correctamente");
    }
}