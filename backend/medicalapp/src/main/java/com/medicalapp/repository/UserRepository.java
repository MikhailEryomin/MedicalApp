package com.medicalapp.repository;

import com.medicalapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.doctor LEFT JOIN FETCH u.patient WHERE u.email = :email")
    Optional<User> findByEmailWithProfiles(String email);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.doctor LEFT JOIN FETCH u.patient WHERE u.id = :id")
    Optional<User> findByIdWithProfiles(Integer id);
}