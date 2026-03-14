package com.medicalapp.repository;

import com.medicalapp.entity.PrescriptionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionLogRepository extends JpaRepository<PrescriptionLog, Integer> {
    long countByPrescriptionId(Integer prescriptionId);
}