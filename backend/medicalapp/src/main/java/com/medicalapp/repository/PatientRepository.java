package com.medicalapp.repository;

import com.medicalapp.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByUserId(Integer userId);

    @Query("SELECT p FROM Patient p JOIN p.doctors d WHERE d.id = :doctorId")
    List<Patient> findByDoctorId(Integer doctorId);
}