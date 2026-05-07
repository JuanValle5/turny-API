package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.business.BusinessHourRequest;
import com.turny.ApiTurny.domain.dto.business.BusinessHourResponse;
import com.turny.ApiTurny.domain.entity.Business;
import com.turny.ApiTurny.domain.entity.BusinessHour;
import com.turny.ApiTurny.domain.entity.User;
import com.turny.ApiTurny.domain.repository.BusinessHourRepository;
import com.turny.ApiTurny.domain.repository.BusinessRepository;
import com.turny.ApiTurny.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessHourService {

    private final BusinessHourRepository businessHourRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;


    private static final String[] DIAS = {
            "Domingo", "Lunes", "Martes", "Miércoles",
            "Jueves", "Viernes", "Sábado"
    };

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    // Obtiene los horarios de un negocio
    public List<BusinessHourResponse> getHorarios(UUID negocioId) {
        return businessHourRepository
                .findByNegocioIdOrderByDiaSemana(negocioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Reemplaza todos los horarios del negocio autenticado
    @Transactional
    public List<BusinessHourResponse> guardarHorarios(
            String emailUsuario,
            List<BusinessHourRequest> request
    ) {
        Business negocio = getNegocioDelUsuario(emailUsuario);

        // Valida que vengan los 7 días
        if (request.size() != 7) {
            throw new IllegalArgumentException("Debes enviar los 7 días de la semana");
        }

        // Valida coherencia de horas en días abiertos
        for (BusinessHourRequest hora : request) {
            if (Boolean.TRUE.equals(hora.abierto())) {
                if (hora.horaApertura() == null || hora.horaCierre() == null) {
                    throw new IllegalArgumentException(
                            "Día " + DIAS[hora.diaSemana()] + " está abierto pero faltan las horas"
                    );
                }
            }
        }

        // Borra los anteriores y guarda los nuevos
        businessHourRepository.deleteByNegocioId(negocio.getId());

        List<BusinessHour> nuevos = request.stream()
                .map(r -> BusinessHour.builder()
                        .negocio(negocio)
                        .diaSemana(r.diaSemana())
                        .abierto(r.abierto())
                        .horaApertura(parsearHora(r.horaApertura()))
                        .horaCierre(parsearHora(r.horaCierre()))
                        .descansoInicio(parsearHora(r.descansoInicio()))
                        .descansoFin(parsearHora(r.descansoFin()))
                        .build()
                )
                .toList();

        return businessHourRepository.saveAll(nuevos)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Helpers
    private Business getNegocioDelUsuario(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        return businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes un negocio asociado"));
    }

    private LocalTime parsearHora(String hora) {
        if (hora == null || hora.isBlank()) return null;
        return LocalTime.parse(hora, FMT);
    }





    private BusinessHourResponse toResponse(BusinessHour h) {
        return new BusinessHourResponse(
                h.getId(),
                h.getDiaSemana(),
                DIAS[h.getDiaSemana()],
                h.getAbierto(),
                h.getHoraApertura() != null ? h.getHoraApertura().format(FMT) : null,
                h.getHoraCierre()   != null ? h.getHoraCierre().format(FMT)   : null,
                h.getDescansoInicio()!= null ? h.getDescansoInicio().format(FMT): null,
                h.getDescansoFin()  != null ? h.getDescansoFin().format(FMT)  : null
        );
    }



}
