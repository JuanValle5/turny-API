package com.turny.ApiTurny.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "businesses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String categoria;

    @Column(nullable = false, length = 12, unique = true)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(length = 20)
    private String whatsapp;

    @Column(length = 255)
    private String email;

    @Column(length = 255)
    private String website;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(nullable = false, precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_resenas", nullable = false)
    @Builder.Default
    private Integer totalResenas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verificado = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Relaciones
    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL)
    private List<Service> services;

    @OneToMany(mappedBy = "negocio", cascade = CascadeType.ALL)
    private List<BusinessHour> businessHours;

    @OneToMany(mappedBy = "negocio")
    private List<Appointment> appointments;
}
