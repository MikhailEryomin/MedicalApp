package com.medicalapp.service;

//import com.medicalapp.dto.CreatePatientRequest;
import com.medicalapp.dto.PatientDetailResponse;
import com.medicalapp.dto.PatientListItem;
import com.medicalapp.entity.*;
//import com.medicalapp.entity.enums.Gender;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.exception.AppException;
import com.medicalapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
//    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
//    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<PatientListItem> getAllPatientsGlobal(String search, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);

        Pageable pageable = PageRequest.of(
                0,
                safeLimit,
                Sort.by(Sort.Direction.ASC, "lastName", "firstName", "id")
        );

        var page = patientRepository.searchAllPatients(search, pageable);

        return page.getContent().stream()
                .map(p -> PatientListItem.builder()
                        .id(p.getId())
                        .firstName(p.getFirstName())
                        .lastName(p.getLastName())
                        .birthDate(p.getBirthDate())
                        .gender(p.getGender() != null ? p.getGender().name() : null)
                        .build())
                .toList();
    }

    @Transactional
    public boolean assignPatientToDoctor(Integer patientId, Doctor doctor) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Patient not found"));
        boolean alreadyLinked = doctorRepository.existsDoctorPatientLink(doctor.getId(), patientId);
        if (alreadyLinked) {
            return false;
        }
        doctor.getPatients().add(patient);
        doctorRepository.save(doctor);
        return true;
    }

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

    private PatientDetailResponse toDetailResponse(Patient p) {
        return PatientDetailResponse.builder()
                .id(p.getId())
                .email(p.getUser() != null ? p.getUser().getEmail() : null)
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .birthDate(p.getBirthDate())
                .gender(p.getGender() != null ? p.getGender().name() : null)
                .allergies(p.getAllergies() != null ? p.getAllergies() : new ArrayList<>())
                .chronicDiseases(p.getChronicDiseases() != null ? p.getChronicDiseases() : new ArrayList<>())
                .build();
    }
}