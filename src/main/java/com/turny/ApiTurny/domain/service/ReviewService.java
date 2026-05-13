package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.review.*;
import com.turny.ApiTurny.domain.entity.*;
import com.turny.ApiTurny.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final BusinessRepository businessRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    // ── CREAR RESEÑA (cliente) ────────────────────────────────────────────────

    @Transactional
    public ReviewResponse crear(String emailCliente, CreateReviewRequest request) {
        Client cliente = getClienteDelUsuario(emailCliente);

        // 1. Verificar que la cita existe y pertenece al cliente
        Appointment cita = appointmentRepository.findById(request.citaId())
                .orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));

        if (!cita.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("Esta cita no te pertenece");
        }

        // 2. Solo se pueden reseñar citas completadas
        if (!"completada".equals(cita.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo puedes reseñar citas completadas"
            );
        }

        // 3. Verificar que no haya reseñado ya esta cita
        if (reviewRepository.existsByCitaId(cita.getId())) {
            throw new IllegalStateException("Ya dejaste una reseña para esta cita");
        }

        // 4. Crear la reseña
        Review review = Review.builder()
                .negocio(cita.getNegocio())
                .cliente(cliente)
                .cita(cita)
                .calificacion(request.calificacion())
                .comentario(request.comentario())
                .build();

        reviewRepository.save(review);

        // 5. Recalcular rating del negocio
        recalcularRating(cita.getNegocio().getId());

        return toResponse(review);
    }

    // ── RESPONDER RESEÑA (negocio) ────────────────────────────────────────────

    @Transactional
    public ReviewResponse responder(
            String emailNegocio, UUID reviewId, ResponderReviewRequest request
    ) {
        Business negocio = getNegocioDelUsuario(emailNegocio);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Reseña no encontrada"));

        // Verificar que la reseña es de su negocio
        if (!review.getNegocio().getId().equals(negocio.getId())) {
            throw new AccessDeniedException("Esta reseña no pertenece a tu negocio");
        }

        // No puede responder dos veces
        if (review.getRespuesta() != null) {
            throw new IllegalStateException("Ya respondiste esta reseña");
        }

        review.setRespuesta(request.respuesta());
        review.setFechaRespuesta(Instant.now());

        return toResponse(reviewRepository.save(review));
    }

    // ── VER RESEÑAS DE UN NEGOCIO (público) ──────────────────────────────────

    public List<ReviewResponse> getResenasNegocio(UUID negocioId) {
        return reviewRepository
                .findByNegocioIdAndVisibleTrueOrderByCreatedAtDesc(negocioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── RESUMEN DE RATING (público) ───────────────────────────────────────────

    public RatingResumen getRatingResumen(UUID negocioId) {
        List<Review> resenas = reviewRepository
                .findByNegocioIdAndVisibleTrueOrderByCreatedAtDesc(negocioId);

        if (resenas.isEmpty()) {
            return new RatingResumen(
                    BigDecimal.ZERO, 0,
                    Map.of(5, 0, 4, 0, 3, 0, 2, 0, 1, 0)
            );
        }

        // Distribución por estrellas
        Map<Integer, Integer> distribucion = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) distribucion.put(i, 0);
        for (Review r : resenas) {
            distribucion.merge(r.getCalificacion().intValue(), 1, Integer::sum);
        }

        double promedio = resenas.stream()
                .mapToInt(r -> r.getCalificacion().intValue())
                .average()
                .orElse(0.0);

        return new RatingResumen(
                BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP),
                resenas.size(),
                distribucion
        );
    }

    // ── MIS RESEÑAS (cliente) ─────────────────────────────────────────────────

    public List<ReviewResponse> getMisResenas(String emailCliente) {
        Client cliente = getClienteDelUsuario(emailCliente);
        return reviewRepository
                .findByClienteIdOrderByCreatedAtDesc(cliente.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    @Transactional
    protected void recalcularRating(UUID negocioId) {
        Business negocio = businessRepository.findById(negocioId)
                .orElseThrow(() -> new NoSuchElementException("Negocio no encontrado"));

        Double promedio = reviewRepository
                .calcularRatingPromedio(negocioId)
                .orElse(0.0);

        Integer total = reviewRepository.contarResenasVisibles(negocioId);

        negocio.setRating(
                BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP)
        );
        negocio.setTotalResenas(total);
        businessRepository.save(negocio);
    }

    private Client getClienteDelUsuario(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes un perfil de cliente"));
    }

    private Business getNegocioDelUsuario(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes un negocio asociado"));
    }

    private String generarEstrellas(Short calificacion) {
        String llena = "★";
        String vacia = "☆";
        return llena.repeat(calificacion) + vacia.repeat(5 - calificacion);
    }

    private ReviewResponse toResponse(Review r) {
        User userCliente = r.getCliente().getUser();
        return new ReviewResponse(
                r.getId(),
                r.getNegocio().getId(),
                userCliente.getNombre(),
                userCliente.getAvatarUrl(),
                r.getCalificacion(),
                generarEstrellas(r.getCalificacion()),
                r.getComentario(),
                r.getRespuesta(),
                r.getFechaRespuesta(),
                r.getCreatedAt()
        );
    }
}
