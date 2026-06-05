package com.peluqueria.gestioncitas.repository;

import com.peluqueria.gestioncitas.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Servicio
 * Proporciona métodos para acceder y manipular datos de servicios
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    /**
     * Busca un servicio por su nombre
     * 
     * @param nombre Nombre del servicio
     * @return Optional con el servicio si existe
     */
    Optional<Servicio> findByNombre(String nombre);

    /**
     * Busca servicios por nombre (búsqueda parcial)
     * 
     * @param nombre Nombre a buscar
     * @return Lista de servicios que contengan el nombre
     */
    List<Servicio> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Verifica si existe un servicio con el nombre dado
     * 
     * @param nombre Nombre a verificar
     * @return true si existe, false si no
     */
    boolean existsByNombre(String nombre);

    /**
     * Obtiene servicios ordenados por precio ascendente
     * 
     * @return Lista de servicios ordenados por precio
     */
    List<Servicio> findAllByOrderByPrecioAsc();

    /**
     * Obtiene servicios ordenados por duración ascendente
     * 
     * @return Lista de servicios ordenados por duración
     */
    List<Servicio> findAllByOrderByDuracionMinutosAsc();

    /**
     * Busca servicios dentro de un rango de precios
     * 
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de servicios en el rango de precios
     */
    @Query("SELECT s FROM Servicio s WHERE s.precio BETWEEN :precioMin AND :precioMax ORDER BY s.precio")
    List<Servicio> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
}