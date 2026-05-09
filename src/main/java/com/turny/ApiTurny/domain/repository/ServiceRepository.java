package com.turny.ApiTurny.domain.repository;

import com.turny.ApiTurny.domain.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    List<Service> findByNegocioIdAndActivoTrueOrderByOrdenAsc(UUID negocioId);
    boolean existsByIdAndNegocioId(UUID id, UUID negocioId);

}
