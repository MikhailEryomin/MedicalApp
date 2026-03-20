// src/main/java/com/medicalapp/service/PatientService.java
package com.medicalapp.service;

import com.medicalapp.dto.CreatePatientRequest;
import com.medicalapp.dto.PatientDetailResponse;
import com.medicalapp.dto.PatientListItem;
import com.medicalapp.entity.*;
import com.medicalapp.entity.enums.Gender;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.exception.AppException;
import com.medicalapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<PatientListItem> getMyPatients(Doctor doctor) {
        List<Patient> patients = patientRepository.findByDoctorId(doctor.getId());
        return patients.stream().map(p -> PatientListItem.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .birthDate(p.getBirthDate())
                .gender(p.getGender() != null ? p.getGender().name() : null)
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientDetailResponse getPatientDetail(Integer patientId, User currentUser) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (currentUser.getRole() == UserRole.PATIENT) {
            if (currentUser.getPatient() == null || !currentUser.getPatient().getId().equals(patientId)) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
        } else if (currentUser.getRole() == UserRole.DOCTOR) {
            if (currentUser.getDoctor() == null) {
                throw new AppException(HttpStatus.FORBIDDEN, "Access denied");
            }
            if (!doctorRepository.existsDoctorPatientLink(currentUser.getDoctor().getId(), patientId)) {
                throw new AppException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
            }
        }

        return toDetailResponse(patient);
    }

    @Transactional
    public PatientDetailResponse createPatient(CreatePatientRequest request, Doctor doctor) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already registered");
        }

        Gender genderEnum = null;
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                genderEnum = Gender.valueOf(request.getGender());
            } catch (IllegalArgumentException e) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Invalid gender. Use MALE or FEMALE.");
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.PATIENT)
                .build();
        userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .gender(genderEnum)
                .allergies(request.getAllergies() != null ? request.getAllergies() : new ArrayList<>())
                .chronicDiseases(request.getChronicDiseases() != null ? request.getChronicDiseases() : new ArrayList<>())
                .build();
        patientRepository.save(patient);

        doctor.getPatients().add(patient);
        doctorRepository.save(doctor);

        return toDetailResponse(patient);
    }

    private PatientDetailResponse toDetailResponse(Patient p) {
        return PatientDetailResponse.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .birthDate(p.getBirthDate())
                .gender(p.getGender() != null ? p.getGender().name() : null)
                .allergies(p.getAllergies() != null ? p.getAllergies() : new ArrayList<>())
                .chronicDiseases(p.getChronicDiseases() != null ? p.getChronicDiseases() : new ArrayList<>())
                .build();
    }
}