// src/main/java/com/medicalapp/service/PrescriptionService.java
package com.medicalapp.service;

import com.medicalapp.dto.*;
import com.medicalapp.entity.*;
import com.medicalapp.entity.enums.PrescriptionStatus;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.exception.AppException;
import com.medicalapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionLogRepository logRepository;
    private final MedicineRepository medicineRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private void checkDoctorPatientLink(Integer doctorId, Integer patientId) {
        if (!doctorRepository.existsDoctorPatientLink(doctorId, patientId)) {
            throw new AppException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }
    }

    private int getTakenCount(Integer prescriptionId) {
        return (int) logRepository.countByPrescriptionId(prescriptionId);
    }

    private PrescriptionResponse toResponse(Prescription p) {
        int takenCount = getTakenCount(p.getId());
        int totalCount = p.getFrequency() * p.getDurationDays();
        String medicineName = p.getMedicine() != null ? p.getMedicine().getName() : null;

        return PrescriptionResponse.builder()
                .id(p.getId())
                .doctorId(p.getDoctor().getId())
                .patientId(p.getPatient().getId())
                .medicineId(p.getMedicine().getId())
                .medicineName(medicineName)
                .dosage(p.getDosage())
                .frequency(p.getFrequency())
                .durationDays(p.getDurationDays())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus().name())
                .notes(p.getNotes())
                .takenCount(takenCount)
                .totalCount(totalCount)
                .build();
    }

    @Transactional
    public PrescriptionResponse create(CreatePrescriptionRequest request, Doctor doctor) {
        checkDoctorPatientLink(doctor.getId(), request.getPatientId());

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Medicine not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));

        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(request.getDurationDays());

        Prescription prescription = Prescription.builder()
                .doctor(doctor)
                .patient(patient)
                .medicine(medicine)
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .durationDays(request.getDurationDays())
                .startDate(today)
                .endDate(end)
                .status(PrescriptionStatus.ACTIVE)
                .notes(request.getNotes())
                .build();
        prescriptionRepository.save(prescription);

        return toResponse(prescription);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPatientPrescriptions(Integer patientId, User currentUser) {
        if (currentUser.getRole() == UserRole.PATIENT) {
            if (currentUser.getPatient() == null || !currentUser.getPatient().getId().equals(patientId)) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
        } else if (currentUser.getRole() == UserRole.DOCTOR) {
            if (currentUser.getDoctor() == null) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
            checkDoctorPatientLink(currentUser.getDoctor().getId(), patientId);
        }

        List<Prescription> prescriptions = prescriptionRepository.findByPatientIdWithMedicine(patientId);
        return prescriptions.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PrescriptionLogResponse markAsTaken(Integer prescriptionId, User currentUser) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Prescription not found"));

        if (currentUser.getRole() == UserRole.PATIENT) {
            if (currentUser.getPatient() == null ||
                    !currentUser.getPatient().getId().equals(prescription.getPatient().getId())) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
        } else if (currentUser.getRole() == UserRole.DOCTOR) {
            if (currentUser.getDoctor() == null) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
            checkDoctorPatientLink(currentUser.getDoctor().getId(), prescription.getPatient().getId());
        } else {
            throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (prescription.getStatus() != PrescriptionStatus.ACTIVE) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Prescription is not active");
        }

        PrescriptionLog log = PrescriptionLog.builder()
                .prescription(prescription)
                .takenAt(LocalDateTime.now())
                .build();
        logRepository.save(log);

        int totalNeeded = prescription.getFrequency() * prescription.getDurationDays();
        int takenCount = getTakenCount(prescriptionId);

        if (takenCount >= totalNeeded) {
            prescription.setStatus(PrescriptionStatus.COMPLETED);
            prescriptionRepository.save(prescription);
        }

        return PrescriptionLogResponse.builder()
                .id(log.getId())
                .prescriptionId(prescriptionId)
                .takenAt(log.getTakenAt())
                .build();
    }

    @Transactional(readOnly = true)
    public TakenCountResponse getTakenCountInfo(Integer prescriptionId, User currentUser) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Prescription not found"));

        if (currentUser.getRole() == UserRole.PATIENT) {
            if (currentUser.getPatient() == null ||
                    !currentUser.getPatient().getId().equals(prescription.getPatient().getId())) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
        } else if (currentUser.getRole() == UserRole.DOCTOR) {
            if (currentUser.getDoctor() == null) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
            checkDoctorPatientLink(currentUser.getDoctor().getId(), prescription.getPatient().getId());
        }

        int totalNeeded = prescription.getFrequency() * prescription.getDurationDays();
        int taken = getTakenCount(prescriptionId);

        return TakenCountResponse.builder()
                .prescriptionId(prescriptionId)
                .takenCount(taken)
                .totalCount(totalNeeded)
                .completed(taken >= totalNeeded)
                .build();
    }
}