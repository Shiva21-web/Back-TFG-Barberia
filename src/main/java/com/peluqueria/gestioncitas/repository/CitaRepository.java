package com.peluqueria.gestioncitas.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.peluqueria.gestioncitas.entity.Cita;
import com.peluqueria.gestioncitas.entity.Cita.EstadoCita;

/**
 * Repositorio para la entidad Cita Proporciona métodos para gestionar citas
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca citas por cliente ID
     *
     * @param idCliente ID del cliente
     * @return Lista de citas del cliente
     */
    List<Cita> findByClienteIdCliente(Long idCliente);

    /**
     * Busca citas por servicio ID
     *
     * @param idServicio ID del servicio
     * @return Lista de citas con ese servicio
     */
    List<Cita> findByServicioIdServicio(Long idServicio);

    /**
     * Busca citas por fecha
     *
     * @param fecha Fecha de las citas
     * @return Lista de citas en esa fecha
     */
    List<Cita> findByFechaOrderByHoraAsc(LocalDate fecha);

    /**
     * Busca citas por estado
     *
     * @param estado Estado de la cita
     * @return Lista de citas con ese estado
     */
    List<Cita> findByEstado(EstadoCita estado);

    /**
     * Busca citas por cliente y estado
     *
     * @param idCliente ID del cliente
     * @param estado Estado de la cita
     * @return Lista de citas que cumplan ambas condiciones
     */
    List<Cita> findByClienteIdClienteAndEstado(Long idCliente, EstadoCita estado);

    /**
     * Busca citas en un rango de fechas
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de citas en el rango
     */
    List<Cita> findByFechaBetweenOrderByFechaAscHoraAsc(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca citas pendientes o confirmadas en una fecha específica Útil para
     * ver la agenda del día
     *
     * @param fecha Fecha a consultar
     * @return Lista de citas activas del día
     */
    @Query("SELECT c FROM Cita c WHERE c.fecha = :fecha "
            + "AND c.estado IN ('CONFIRMADA') "
            + "ORDER BY c.hora ASC")
    List<Cita> findCitasActivasByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Verifica si existe una cita en una fecha y hora específica Útil para
     * prevenir conflictos de horarios
     *
     * @param fecha Fecha de la cita
     * @param hora Hora de la cita
     * @return true si existe una cita, false si no
     */
    boolean existsByFechaAndHora(LocalDate fecha, LocalTime hora);

    /**
     * Busca una cita específica por fecha, hora y estado
     *
     * @param fecha Fecha de la cita
     * @param hora Hora de la cita
     * @param estado Estado de la cita
     * @return Optional con la cita si existe
     */
    Optional<Cita> findByFechaAndHoraAndEstado(LocalDate fecha, LocalTime hora, EstadoCita estado);

    /**
     * Cuenta cuántas citas tiene un cliente
     *
     * @param idCliente ID del cliente
     * @return Número de citas del cliente
     */
    long countByClienteIdCliente(Long idCliente);

    /**
     * Cuenta cuántas citas de un estado tiene un cliente
     *
     * @param idCliente ID del cliente
     * @param estado Estado de las citas
     * @return Número de citas del cliente con ese estado
     */
    long countByClienteIdClienteAndEstado(Long idCliente, EstadoCita estado);

    /**
     * Obtiene las próximas citas (fecha >= hoy) ordenadas por fecha y hora
     *
     * @param fechaActual Fecha actual
     * @return Lista de citas futuras
     */
    @Query("SELECT c FROM Cita c WHERE c.fecha >= :fechaActual "
            + "AND c.estado IN ('CONFIRMADA') "
            + "ORDER BY c.fecha ASC, c.hora ASC")
    List<Cita> findProximasCitas(@Param("fechaActual") LocalDate fechaActual);

    /**
     * Cuenta las citas activas (PENDIENTE o CONFIRMADA) de un cliente en una
     * fecha específica Útil para validar límite de citas por día
     *
     * @param idCliente ID del cliente
     * @param fecha Fecha a consultar
     * @return Número de citas activas del cliente en esa fecha
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.cliente.idCliente = :idCliente "
            + "AND c.fecha = :fecha "
+ "AND c.estado IN ('CONFIRMADA')")
    long contarCitasActivasClientePorFecha(@Param("idCliente") Long idCliente,
            @Param("fecha") LocalDate fecha);
}
