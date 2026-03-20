package com.medicalapp.repository;

import com.medicalapp.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByUserId(Integer userId);

    @Query("SELECT CASE WHEN COUNT(dp) > 0 THEN true ELSE false END " +
            "FROM Doctor d JOIN d.patients dp WHERE d.id = :doctorId AND dp.id = :patientId")
    boolean existsDoctorPatientLink(Integer doctorId, Integer patientId);
}