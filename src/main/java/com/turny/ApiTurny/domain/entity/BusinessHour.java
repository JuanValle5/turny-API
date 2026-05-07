package com.turny.ApiTurny.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "business_hours",
        uniqueConstraints = @UniqueConstraint(columnNames = {"negocio_id","dia_semana"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Business negocio;

    // 0=Domingo, 1=Lunes, ..., 6=Sábado
    @Column(name = "dia_semana", nullable = false)
    private Short diaSemana;

    @Column(nullable = false)
    private Boolean abierto = false;

    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    @Column(name = "descanso_inicio")
    private LocalTime descansoInicio;

    @Column(name = "descanso_fin")
    private LocalTime descansoFin;
}
