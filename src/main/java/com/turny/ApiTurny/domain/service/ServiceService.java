package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.service.*;
import com.turny.ApiTurny.domain.entity.*;
import com.turny.ApiTurny.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    // GET público — servicios activos de un negocio
    public List<ServiceResponse> getServicios(UUID negocioId) {
        return serviceRepository
                .findByNegocioIdAndActivoTrueOrderByOrdenAsc(negocioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // POST — crear servicio
    @Transactional
    public ServiceResponse crear(String emailUsuario, ServiceRequest request) {
        Business negocio = getNegocioDelUsuario(emailUsuario);

        com.turny.ApiTurny.domain.entity.Service servicio =
                com.turny.ApiTurny.domain.entity.Service.builder()
                        .negocio(negocio)
                        .nombre(request.nombre())
                        .descripcion(request.descripcion())
                        .duracion(request.duracion())
                        .precio(request.precio())
                        .precioDesde(request.precioDesde() != null ? request.precioDesde() : false)
                        .categoria(request.categoria())
                        .imagenUrl(request.imagenUrl())
                        .orden(request.orden() != null ? request.orden() : (short) 0)
                        .build();

        return toResponse(serviceRepository.save(servicio));
    }

    // PUT — editar servicio
    @Transactional
    public ServiceResponse editar(String emailUsuario, UUID servicioId, ServiceRequest request) {
        Business negocio = getNegocioDelUsuario(emailUsuario);
        verificarPropietario(servicioId, negocio.getId());

        com.turny.ApiTurny.domain.entity.Service servicio = serviceRepository.findById(servicioId)
                .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado"));

        servicio.setNombre(request.nombre());
        servicio.setDescripcion(request.descripcion());
        servicio.setDuracion(request.duracion());
        servicio.setPrecio(request.precio());
        servicio.setPrecioDesde(request.precioDesde() != null ? request.precioDesde() : false);
        servicio.setCategoria(request.categoria());
        servicio.setImagenUrl(request.imagenUrl());
        if (request.orden() != null) servicio.setOrden(request.orden());

        return toResponse(serviceRepository.save(servicio));
    }

    // DELETE — desactiva el servicio (soft delete)
    @Transactional
    public void desactivar(String emailUsuario, UUID servicioId) {
        Business negocio = getNegocioDelUsuario(emailUsuario);
        verificarPropietario(servicioId, negocio.getId());

        com.turny.ApiTurny.domain.entity.Service servicio = serviceRepository.findById(servicioId)
                .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado"));

        servicio.setActivo(false);
        serviceRepository.save(servicio);
    }

    // Helpers
    private Business getNegocioDelUsuario(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        return businessRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("No tienes un negocio asociado"));
    }

    private void verificarPropietario(UUID servicioId, UUID negocioId) {
        if (!serviceRepository.existsByIdAndNegocioId(servicioId, negocioId)) {
            throw new AccessDeniedException("Este servicio no pertenece a tu negocio");
        }
    }

    private String formatearDuracion(Short minutos) {
        if (minutos < 60) return minutos + " min";
        int horas = minutos / 60;
        int mins = minutos % 60;
        return mins == 0 ? horas + "h" : horas + "h " + mins + "min";
    }

    private ServiceResponse toResponse(com.turny.ApiTurny.domain.entity.Service s) {
        return new ServiceResponse(
                s.getId(),
                s.getNombre(),
                s.getDescripcion(),
                s.getDuracion(),
                formatearDuracion(s.getDuracion()),
                s.getPrecio(),
                s.getPrecioDesde(),
                s.getCategoria(),
                s.getImagenUrl(),
                s.getOrden(),
                s.getActivo()
        );
    }
}