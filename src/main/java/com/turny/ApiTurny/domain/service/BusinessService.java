package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.business.BusinessCardResponse;
import com.turny.ApiTurny.domain.entity.*;
import com.turny.ApiTurny.domain.repository.*;
import com.turny.ApiTurny.domain.dto.profile.NegocioProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


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
    public BusinessCardResponse getByCode(String codigo) {
        int diaHoy = LocalDate.now().getDayOfWeek().getValue() % 7;

        Business negocio = businessRepository.findByCodigo(codigo)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe ningún negocio con el código: " + codigo
                ));

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
    }
    public NegocioProfileResponse getPerfilPublico(UUID negocioId) {
        Business negocio = businessRepository.findById(negocioId)
                .orElseThrow(() -> new NoSuchElementException("Negocio no encontrado"));

        User user = negocio.getUser();

        return new NegocioProfileResponse(
                user.getId(),
                negocio.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getTelefono(),
                user.getAvatarUrl(),
                negocio.getNombre(),
                negocio.getCategoria(),
                negocio.getDescripcion(),
                negocio.getDireccion(),
                negocio.getCiudad(),
                negocio.getCodigoPostal(),
                negocio.getLatitud(),
                negocio.getLongitud(),
                negocio.getWhatsapp(),
                negocio.getEmail(),
                negocio.getWebsite(),
                negocio.getImagenUrl(),
                negocio.getLogoUrl(),
                negocio.getCodigo(),
                negocio.getRating(),
                negocio.getTotalResenas(),
                negocio.getVerificado(),
                negocio.getCreatedAt()
        );
    }
}
