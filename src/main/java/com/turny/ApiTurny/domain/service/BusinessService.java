package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.business.BusinessCardResponse;
import com.turny.ApiTurny.domain.entity.*;
import com.turny.ApiTurny.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessHourRepository businessHourRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("H:mm");

    public List<BusinessCardResponse> getCards() {
        // Día de la semana actual (0=Dom, 1=Lun, ..., 6=Sáb)
        int diaHoy = LocalDate.now().getDayOfWeek().getValue() % 7;

        return businessRepository.findByActivoTrueOrderByNombreAsc()
                .stream()
                .map(negocio -> {
                    String horarioHoy = businessHourRepository
                            .findByNegocioIdOrderByDiaSemana(negocio.getId())
                            .stream()
                            .filter(h -> h.getDiaSemana() == diaHoy)
                            .findFirst()
                            .map(h -> {
                                if (!h.getAbierto()) return "Cerrado hoy";
                                return h.getHoraApertura().format(FMT)
                                        + " - "
                                        + h.getHoraCierre().format(FMT);
                            })
                            .orElse("Horario no disponible");

                    return new BusinessCardResponse(
                            negocio.getId(),
                            negocio.getNombre(),
                            negocio.getCategoria(),
                            negocio.getDireccion(),
                            negocio.getLogoUrl(),
                            negocio.getRating(),
                            negocio.getTotalResenas(),
                            horarioHoy
                    );
                })
                .toList();
    }
}
