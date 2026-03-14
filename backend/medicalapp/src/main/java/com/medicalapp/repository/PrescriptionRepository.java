package com.medicalapp.repository;

import com.medicalapp.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    @Query("SELECT p FROM Prescription p JOIN FETCH p.medicine WHERE p.patient.id = :patientId ORDER BY p.startDate DESC")
    List<Prescription> findByPatientIdWithMedicine(Integer patientId);

    @Query("SELECT p FROM Prescription p JOIN FETCH p.medicine WHERE p.id = :id")
    Optional<Prescription> findByIdWithMedicine(Integer id);
}