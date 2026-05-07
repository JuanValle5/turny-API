package com.turny.ApiTurny.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Business negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Client cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Service servicio;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private Short duracion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // "pendiente"|"confirmada"|"completada"|"cancelada"|"no_asistio"
    @Column(nullable = false, length = 15)
    @Builder.Default
    private String estado = "pendiente";

    @Column(name = "notas_cliente", columnDefinition = "TEXT")
    private String notasCliente;

    @Column(name = "notas_negocio", columnDefinition = "TEXT")
    private String notasNegocio;

    @Column(name = "cancelado_por", length = 10)
    private String canceladoPor;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    @Column(name = "fecha_confirmacion")
    private Instant fechaConfirmacion;

    @Column(name = "fecha_completado")
    private Instant fechaCompletado;

    @Column(name = "fecha_cancelacion")
    private Instant fechaCancelacion;

    @Column(name = "recordatorio_enviado", nullable = false)
    @Builder.Default
    private Boolean recordatorioEnviado = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
