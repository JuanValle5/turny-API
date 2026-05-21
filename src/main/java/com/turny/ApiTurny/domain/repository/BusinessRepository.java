package com.turny.ApiTurny.domain.repository;

import com.turny.ApiTurny.domain.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Optional<Business> findByUserId(UUID userId);
    Optional<Business> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    // Agrega en BusinessRepository.java
    List<Business> findByActivoTrueOrderByNombreAsc();

}
