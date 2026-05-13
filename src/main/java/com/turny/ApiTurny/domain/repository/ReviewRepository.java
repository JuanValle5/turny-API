package com.turny.ApiTurny.domain.repository;

import com.turny.ApiTurny.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // Reseñas visibles de un negocio ordenadas por fecha
    List<Review> findByNegocioIdAndVisibleTrueOrderByCreatedAtDesc(UUID negocioId);

    // Verifica si el cliente ya reseñó esa cita
    boolean existsByCitaId(UUID citaId);

    // Reseñas que ha dejado un cliente
    List<Review> findByClienteIdOrderByCreatedAtDesc(UUID clienteId);

    // Para recalcular el rating del negocio
    @Query("""
        SELECT AVG(r.calificacion) FROM Review r
        WHERE r.negocio.id = :negocioId
        AND r.visible = true
    """)
    Optional<Double> calcularRatingPromedio(@Param("negocioId") UUID negocioId);

    @Query("""
        SELECT COUNT(r) FROM Review r
        WHERE r.negocio.id = :negocioId
        AND r.visible = true
    """)
    Integer contarResenasVisibles(@Param("negocioId") UUID negocioId);
}
