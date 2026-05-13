package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.favorite.FavoriteResponse;
import com.turny.ApiTurny.domain.dto.favorite.ToggleFavoriteResponse;
import com.turny.ApiTurny.domain.entity.Business;
import com.turny.ApiTurny.domain.entity.User;
import com.turny.ApiTurny.domain.entity.Client;
import com.turny.ApiTurny.domain.entity.Favorite;
import com.turny.ApiTurny.domain.repository.BusinessRepository;
import com.turny.ApiTurny.domain.repository.ClientRepository;
import com.turny.ApiTurny.domain.repository.FavoriteRepository;
import com.turny.ApiTurny.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BusinessRepository businessRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    // ── MIS FAVORITOS (cliente) ───────────────────────────────────────────────

    public List<FavoriteResponse> getMisFavoritos(String emailCliente) {
        Client cliente = getClienteDelUsuario(emailCliente);
        return favoriteRepository
                .findByClienteIdOrderByCreatedAtDesc(cliente.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── TOGGLE — agrega si no existe, elimina si ya existe ───────────────────

    @Transactional
    public ToggleFavoriteResponse toggle(String emailCliente, UUID negocioId) {
        Client cliente = getClienteDelUsuario(emailCliente);

        Business negocio = businessRepository.findById(negocioId)
                .orElseThrow(() -> new NoSuchElementException("Negocio no encontrado"));

        Optional<Favorite> existente = favoriteRepository
                .findByClienteIdAndNegocioId(cliente.getId(), negocio.getId());

        // Si ya es favorito lo elimina
        if (existente.isPresent()) {
            favoriteRepository.delete(existente.get());
            return new ToggleFavoriteResponse(
                    negocioId, false, "Negocio eliminado de favoritos"
            );
        }

        // Si no existe lo agrega
        favoriteRepository.save(
                Favorite.builder()
                        .cliente(cliente)
                        .negocio(negocio)
                        .build()
        );

        return new ToggleFavoriteResponse(
                negocioId, true, "Negocio agregado a favoritos"
        );
    }

    // ── VERIFICAR si un negocio es favorito ───────────────────────────────────

    public boolean esFavorito(String emailCliente, UUID negocioId) {
        Client cliente = getClienteDelUsuario(emailCliente);
        return favoriteRepository.existsByClienteIdAndNegocioId(
                cliente.getId(), negocioId
        );
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Client getClienteDelUsuario(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes un perfil de cliente"));
    }

    private FavoriteResponse toResponse(Favorite f) {
        Business n = f.getNegocio();
        return new FavoriteResponse(
                f.getId(),
                n.getId(),
                n.getNombre(),
                n.getCategoria(),
                n.getDireccion(),
                n.getImagenUrl(),
                n.getLogoUrl(),
                n.getRating(),
                n.getTotalResenas(),
                f.getCreatedAt()
        );
    }
}