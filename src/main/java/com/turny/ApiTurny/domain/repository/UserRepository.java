package com.turny.ApiTurny.domain.repository;

import com.turny.ApiTurny.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.client
    LEFT JOIN FETCH u.business
    WHERE u.email = :email
""")
    Optional<User> findByEmailWithPerfil(@Param("email") String email);
}
