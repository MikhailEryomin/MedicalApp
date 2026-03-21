package com.medicalapp.repository;

import com.medicalapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.doctor LEFT JOIN FETCH u.patient WHERE u.emailLookup = :lookup")
    Optional<User> findByEmailLookupWithProfiles(@Param("lookup") String lookup);

    boolean existsByEmailLookup(String lookup);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.doctor LEFT JOIN FETCH u.patient WHERE u.id = :id")
    Optional<User> findByIdWithProfiles(Integer id);

    List<User> findAllByEmailLookupIsNull();
}