package com.peluqueria.gestioncitas.repository;

import com.peluqueria.gestioncitas.entity.UsuarioAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad UsuarioAdmin
 */
@Repository
public interface UsuarioAdminRepository extends JpaRepository<UsuarioAdmin, Long> {

    /**
     * Busca un usuario admin por su username
     * @param username El username del admin
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioAdmin> findByUsername(String username);

    /**
     * Busca un usuario admin por su email
     * @param email El email del admin
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioAdmin> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el username dado
     * @param username El username a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);
}