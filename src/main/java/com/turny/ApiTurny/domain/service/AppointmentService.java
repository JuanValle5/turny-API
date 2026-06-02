package com.turny.ApiTurny.domain.service;

import com.turny.ApiTurny.domain.dto.appointment.*;
import com.turny.ApiTurny.domain.entity.*;
import com.turny.ApiTurny.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BusinessRepository businessRepository;
    private final BusinessHourRepository businessHourRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("hh:mm a");

    // -- CREAR CITA (cliente) -----------------------------------

    @Transactional
    public AppointmentResponse crear(String emailCliente, CreateAppointmentRequest request) {
        // 1. Obtener cliente
        Client cliente = getClienteDelUsuario(emailCliente);

        // 2. Obtener negocio y servicio
        Business negocio = businessRepository.findById(request.negocioId())
                .orElseThrow(() -> new NoSuchElementException("Negocio no encontrado"));

        com.turny.ApiTurny.domain.entity.Service servicio =
                serviceRepository.findById(request.servicioId())
                        .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado"));

        // 3. Validar que el servicio pertenece al negocio
        if (!servicio.getNegocio().getId().equals(negocio.getId())) {
            throw new IllegalArgumentException("El servicio no pertenece a este negocio");
        }

        // 4. Validar que el negocio abre ese día
        int diaSemana = request.fecha().getDayOfWeek().getValue() % 7; // 0=Dom
        BusinessHour horario = businessHourRepository
                .findByNegocioIdOrderByDiaSemana(negocio.getId())
                .stream()
                .filter(h -> h.getDiaSemana() == diaSemana)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No hay horario configurado para ese día"));

        if (!horario.getAbierto()) {
            throw new IllegalArgumentException("El negocio no atiende ese día");
        }

        // 5. Validar que la hora está dentro del horario
        LocalTime horaFin = request.hora().plusMinutes(servicio.getDuracion());
        validarDentroDeHorario(request.hora(), horaFin, horario);

        // 6. Validar que no hay conflicto con otra cita
        if (appointmentRepository.existeConflicto(
                negocio.getId(), request.fecha(), request.hora()
        )) {
            throw new IllegalArgumentException("Ese horario ya está reservado");
        }

        // 7. Crear la cita
        Appointment cita = Appointment.builder()
                .negocio(negocio)
                .cliente(cliente)
                .servicio(servicio)
                .fecha(request.fecha())
                .hora(request.hora())
                .duracion(servicio.getDuracion())
                .precio(servicio.getPrecio())
                .notasCliente(request.notasCliente())
                .build();

        return toResponse(appointmentRepository.save(cita));
    }

    // ── SLOTS DISPONIBLES ─────────────────────────────────────────────────────

    public List<SlotResponse> getSlotsDisponibles(
            UUID negocioId, UUID servicioId, LocalDate fecha
    ) {
        com.turny.ApiTurny.domain.entity.Service servicio = serviceRepository.findById(servicioId)
                .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado"));

        int diaSemana = fecha.getDayOfWeek().getValue() % 7;
        Optional<BusinessHour> horarioOpt = businessHourRepository
                .findByNegocioIdOrderByDiaSemana(negocioId)
                .stream()
                .filter(h -> h.getDiaSemana() == diaSemana)
                .findFirst();

        // Si no abre ese día devuelve lista vacía
        if (horarioOpt.isEmpty() || !horarioOpt.get().getAbierto()) {
            return List.of();
        }

        BusinessHour horario = horarioOpt.get();
        List<Appointment> citasDelDia = appointmentRepository
                .findCitasActivasPorFecha(negocioId, fecha);

        // Horas ya ocupadas
        Set<LocalTime> ocupadas = new HashSet<>();
        for (Appointment cita : citasDelDia) {
            // Marca como ocupados todos los slots que cubre la cita existente
            LocalTime t = cita.getHora();
            while (t.isBefore(cita.getHora().plusMinutes(cita.getDuracion()))) {
                ocupadas.add(t);
                t = t.plusMinutes(servicio.getDuracion());
            }
        }

        // Genera slots cada [duracion del servicio] minutos
        List<SlotResponse> slots = new ArrayList<>();
        LocalTime cursor = horario.getHoraApertura();
        LocalTime cierre = horario.getHoraCierre();

        while (!cursor.plusMinutes(servicio.getDuracion()).isAfter(cierre)) {
            // Saltar descanso si está configurado
            if (estaEnDescanso(cursor, horario)) {
                cursor = cursor.plusMinutes(servicio.getDuracion());
                continue;
            }

//            boolean disponible = !ocupadas.contains(cursor)
//                    && !fecha.equals(LocalDate.now())
//                    || cursor.isAfter(LocalTime.now());
            boolean disponible =
                    !ocupadas.contains(cursor)
                            &&
                            (
                                    !fecha.equals(LocalDate.now())
                                            ||
                                            cursor.isAfter(LocalTime.now())
                            );

            slots.add(new SlotResponse(
                    cursor,
                    cursor.format(FMT_HORA),
                    disponible
            ));
            cursor = cursor.plusMinutes(servicio.getDuracion());
        }

        return slots;
    }

    // ── MIS CITAS (cliente) ───────────────────────────────────────────────────

    public List<AppointmentResponse> getMisCitas(String emailCliente) {
        Client cliente = getClienteDelUsuario(emailCliente);
        return appointmentRepository
                .findByClienteIdOrderByFechaDescHoraDesc(cliente.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── CITAS DEL NEGOCIO ─────────────────────────────────────────────────────

    public List<AppointmentResponse> getCitasNegocio(
            String emailNegocio, LocalDate fecha
    ) {
        Business negocio = getNegocioDelUsuario(emailNegocio);
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        return appointmentRepository
                .findByNegocioIdAndFechaOrderByHoraAsc(negocio.getId(), fechaConsulta)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── CAMBIAR ESTADO ────────────────────────────────────────────────────────

    @Transactional
    public AppointmentResponse cambiarEstado(
            String emailUsuario, UUID citaId, CambiarEstadoRequest request
    ) {
        Appointment cita = appointmentRepository.findById(citaId)
                .orElseThrow(() -> new NoSuchElementException("Cita no encontrada"));

        User user = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        // Valida que quien cambia el estado es el cliente o el negocio de la cita
        boolean esCliente = user.getClient() != null
                && user.getClient().getId().equals(cita.getCliente().getId());
        boolean esNegocio = user.getBusiness() != null
                && user.getBusiness().getId().equals(cita.getNegocio().getId());

        if (!esCliente && !esNegocio) {
            throw new AccessDeniedException("No tienes permiso para modificar esta cita");
        }

        // Valida transiciones de estado permitidas
        validarTransicion(cita.getEstado(), request.estado(), esCliente);

        if ("cancelada".equals(request.estado())) {
            cita.setMotivoCancelacion(request.motivo());
            cita.setCanceladoPor(esCliente ? "client" : "business");
            cita.setFechaCancelacion(java.time.Instant.now());
        } else if ("completada".equals(request.estado())) {
            cita.setFechaCompletado(java.time.Instant.now());
        } else if ("confirmada".equals(request.estado())) {
            cita.setFechaConfirmacion(java.time.Instant.now());
        }

        cita.setEstado(request.estado());
        return toResponse(appointmentRepository.save(cita));
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private void validarTransicion(
            String estadoActual, String nuevoEstado, boolean esCliente
    ) {
        // Mapa de transiciones permitidas
        Map<String, List<String>> transiciones = Map.of(
                "pendiente",   List.of("confirmada", "cancelada"),
                "confirmada",  List.of("completada", "cancelada", "no_asistio"),
                "completada",  List.of(),
                "cancelada",   List.of(),
                "no_asistio",  List.of()
        );

        List<String> permitidos = transiciones.getOrDefault(estadoActual, List.of());
        if (!permitidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException(
                    "No se puede pasar de '" + estadoActual + "' a '" + nuevoEstado + "'"
            );
        }

        // Solo el negocio puede marcar no_asistio o completada
        if (esCliente && (
                "no_asistio".equals(nuevoEstado) || "completada".equals(nuevoEstado)
        )) {
            throw new AccessDeniedException("Solo el negocio puede realizar esta acción");
        }
    }

    private void validarDentroDeHorario(
            LocalTime hora, LocalTime horaFin, BusinessHour horario
    ) {
        if (hora.isBefore(horario.getHoraApertura())) {
            throw new IllegalArgumentException("La hora es antes de la apertura del negocio");
        }
        if (horaFin.isAfter(horario.getHoraCierre())) {
            throw new IllegalArgumentException("La cita termina después del cierre del negocio");
        }
    }

    private boolean estaEnDescanso(LocalTime hora, BusinessHour horario) {
        if (horario.getDescansoInicio() == null || horario.getDescansoFin() == null) {
            return false;
        }
        return !hora.isBefore(horario.getDescansoInicio())
                && hora.isBefore(horario.getDescansoFin());
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

    private String formatearDuracion(Short minutos) {
        if (minutos < 60) return minutos + " min";
        int h = minutos / 60;
        int m = minutos % 60;
        return m == 0 ? h + "h" : h + "h " + m + "min";
    }

    private AppointmentResponse toResponse(Appointment a) {
        LocalTime horaFin = a.getHora().plusMinutes(a.getDuracion());
        return new AppointmentResponse(
                a.getId(),
                a.getNegocio().getId(),
                a.getNegocio().getNombre(),
                a.getNegocio().getImagenUrl(),
                a.getServicio().getId(),
                a.getServicio().getNombre(),
                a.getDuracion(),
                formatearDuracion(a.getDuracion()),
                a.getFecha(),
                a.getHora(),
                horaFin,
                a.getPrecio(),
                a.getEstado(),
                a.getNotasCliente(),
                a.getNotasNegocio()
        );
    }
}
