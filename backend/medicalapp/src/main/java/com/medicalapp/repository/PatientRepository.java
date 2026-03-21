package com.medicalapp.repository;

import com.medicalapp.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    @Query("SELECT p FROM Patient p JOIN p.doctors d WHERE d.id = :doctorId")
    List<Patient> findByDoctorId(Integer doctorId);

    @Query("""
        SELECT p FROM Patient p
        WHERE (:q IS NULL OR :q = ''
           OR lower(p.firstName) LIKE lower(concat('%', :q, '%'))
           OR lower(p.lastName)  LIKE lower(concat('%', :q, '%')))
        """)
    Page<Patient> searchAllPatients(@Param("q") String q, Pageable pageable);
}