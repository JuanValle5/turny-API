package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.profile.*;
import com.turny.ApiTurny.domain.entity.*;
import com.turny.ApiTurny.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final BusinessRepository businessRepository;

    // ── VER PERFIL ────────────────────────────────────────────────────────────

    public ClienteProfileResponse getPerfilCliente(String email) {
        User user = getUserConPerfil(email);
        Client cliente = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes perfil de cliente"));
        return toClienteResponse(user, cliente);
    }

    public NegocioProfileResponse getPerfilNegocio(String email) {
        User user = getUserConPerfil(email);
        Business negocio = businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes perfil de negocio"));
        return toNegocioResponse(user, negocio);
    }

    // ── EDITAR PERFIL ─────────────────────────────────────────────────────────

    @Transactional
    public ClienteProfileResponse updatePerfilCliente(
            String email, UpdateClienteRequest request
    ) {
        User user = getUserConPerfil(email);
        Client cliente = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes perfil de cliente"));

        // Actualiza datos del User
        user.setNombre(request.nombre());
        user.setTelefono(request.telefono());
        user.setAvatarUrl(request.avatarUrl());
        userRepository.save(user);

        // Actualiza datos del Client
        cliente.setFechaNacimiento(request.fechaNacimiento());
        cliente.setGenero(request.genero());
        cliente.setDireccion(request.direccion());
        cliente.setNotas(request.notas());
        clientRepository.save(cliente);

        return toClienteResponse(user, cliente);
    }

    @Transactional
    public NegocioProfileResponse updatePerfilNegocio(
            String email, UpdateNegocioRequest request
    ) {
        User user = getUserConPerfil(email);
        Business negocio = businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes perfil de negocio"));

        // Actualiza datos del User
        user.setNombre(request.nombre());
        user.setTelefono(request.telefono());
        user.setAvatarUrl(request.avatarUrl());
        userRepository.save(user);

        // Actualiza datos del Business
        negocio.setNombre(request.nombreNegocio());
        negocio.setCategoria(request.categoria());
        negocio.setDescripcion(request.descripcion());
        negocio.setDireccion(request.direccion());
        negocio.setCiudad(request.ciudad());
        negocio.setCodigoPostal(request.codigoPostal());
        negocio.setLatitud(request.latitud());
        negocio.setLongitud(request.longitud());
        negocio.setTelefono(request.telefono());
        negocio.setWhatsapp(request.whatsapp());
        negocio.setEmail(request.emailNegocio());
        negocio.setWebsite(request.website());
        negocio.setImagenUrl(request.imagenUrl());
        negocio.setLogoUrl(request.logoUrl());
        businessRepository.save(negocio);

        return toNegocioResponse(user, negocio);
    }

    // ── CAMBIAR CONTRASEÑA ────────────────────────────────────────────────────

    @Transactional
    public void cambiarPassword(
            String email,
            String passwordActual,
            String passwordNuevo,
            org.springframework.security.crypto.password.PasswordEncoder encoder
    ) {
        User user = getUserConPerfil(email);

        if (!encoder.matches(passwordActual, user.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (passwordNuevo.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener mínimo 8 caracteres");
        }

        user.setPasswordHash(encoder.encode(passwordNuevo));
        userRepository.save(user);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private User getUserConPerfil(String email) {
        return userRepository.findByEmailWithPerfil(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    private ClienteProfileResponse toClienteResponse(User user, Client cliente) {
        return new ClienteProfileResponse(
                user.getId(),
                cliente.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getTelefono(),
                user.getAvatarUrl(),
                cliente.getFechaNacimiento(),
                cliente.getGenero(),
                cliente.getDireccion(),
                cliente.getNotas(),
                cliente.getTotalCitas(),
                cliente.getTotalGastado(),
                cliente.getCreatedAt()
        );
    }

    private NegocioProfileResponse toNegocioResponse(User user, Business negocio) {
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