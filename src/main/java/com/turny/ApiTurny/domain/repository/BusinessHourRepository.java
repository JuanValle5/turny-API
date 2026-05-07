package com.turny.ApiTurny.domain.repository;

import com.turny.ApiTurny.domain.entity.BusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, UUID> {
    List<BusinessHour> findByNegocioIdOrderByDiaSemana(UUID negocioId);
    void deleteByNegocioId(UUID negocioId);
}
