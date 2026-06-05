package com.peluqueria.gestioncitas.repository;

import com.peluqueria.gestioncitas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente
 * 
 * @Repository: Indica que esta interfaz es un componente de Spring de tipo repositorio
 * JpaRepository<Cliente, Long>: Proporciona métodos CRUD automáticos
 * - Cliente: Tipo de entidad
 * - Long: Tipo de la clave primaria
 * 
 * Spring Data JPA genera automáticamente la implementación en tiempo de ejecución
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un cliente por su email
     * Método de consulta derivado: Spring genera la consulta automáticamente
     * basándose en el nombre del método
     * 
     * @param email Email del cliente a buscar
     * @return Optional con el cliente si existe, Optional.empty() si no
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Busca un cliente por su teléfono
     * 
     * @param telefono Teléfono del cliente a buscar
     * @return Optional con el cliente si existe, Optional.empty() si no
     */
    Optional<Cliente> findByTelefono(String telefono);

    /**
     * Busca clientes por apellidos (búsqueda parcial, ignora mayúsculas/minúsculas)
     * LIKE %apellidos%
     * 
     * @param apellidos Apellidos a buscar
     * @return Lista de clientes cuyos apellidos contengan el texto buscado
     */
    List<Cliente> findByApellidosContainingIgnoreCase(String apellidos);

    /**
     * Busca clientes por nombre (búsqueda parcial, ignora mayúsculas/minúsculas)
     * 
     * @param nombre Nombre a buscar
     * @return Lista de clientes cuyos nombres contengan el texto buscado
     */
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca clientes por nombre o apellidos usando consulta personalizada
     * 
     * @param searchTerm Término de búsqueda
     * @return Lista de clientes que coincidan con el término
     */
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Cliente> searchByNombreOrApellidos(@Param("searchTerm") String searchTerm);

    /**
     * Verifica si existe un cliente con el email dado
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un cliente con el teléfono dado
     * 
     * @param telefono Teléfono a verificar
     * @return true si existe, false si no
     */
    boolean existsByTelefono(String telefono);

    /**
     * Busca un cliente por su nombre exacto
     * Usado para el login de clientes
     * 
     * @param nombre Nombre del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByNombre(String nombre);

    /**
     * Busca un cliente por nombre y contraseña
     * Usado para autenticación de clientes
     * 
     * @param nombre Nombre del cliente
     * @param contrasena Contraseña del cliente
     * @return Optional con el cliente si las credenciales son correctas
     */
    Optional<Cliente> findByNombreAndContrasena(String nombre, String contrasena);
}
