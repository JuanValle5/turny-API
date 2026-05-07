package com.turny.ApiTurny.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 15)
    private String genero;

    @Column(length = 255)
    private String direccion;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "total_citas", nullable = false)
    @Builder.Default
    private Integer totalCitas = 0;

    @Column(name = "total_gastado", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalGastado = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Relaciones
    @OneToMany(mappedBy = "cliente")
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Favorite> favorites;
}