// src/main/java/com/medicalapp/service/AuthService.java
package com.medicalapp.service;

import com.medicalapp.dto.*;
import com.medicalapp.entity.*;
import com.medicalapp.entity.enums.Gender;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.exception.AppException;
import com.medicalapp.repository.*;
import com.medicalapp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmailWithProfiles(request.getEmail())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!user.getRole().name().equals(request.getRole())) {
            throw new AppException(HttpStatus.UNAUTHORIZED,
                    "This account is registered as " + user.getRole().name() + ", not " + request.getRole());
        }

        String token = jwtTokenProvider.createToken(Map.of(
                "user_id", user.getId(),
                "role", user.getRole().name()
        ));

        Integer profileId = null;
        if (user.getRole() == UserRole.DOCTOR && user.getDoctor() != null) {
            profileId = user.getDoctor().getId();
        } else if (user.getRole() == UserRole.PATIENT && user.getPatient() != null) {
            profileId = user.getPatient().getId();
        }

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("role", user.getRole().name());
        userMap.put("profile_id", profileId);

        return new TokenResponse(token, userMap);
    }

    @Transactional
    public TokenResponse registerDoctor(RegisterDoctorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.DOCTOR)
                .build();
        userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .licenceNumber(request.getLicenceNumber())
                .build();
        doctorRepository.save(doctor);

        String token = jwtTokenProvider.createToken(Map.of(
                "user_id", user.getId(),
                "role", user.getRole().name()
        ));

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("role", user.getRole().name());
        userMap.put("profile_id", doctor.getId());

        return new TokenResponse(token, userMap);
    }

    @Transactional
    public TokenResponse registerPatientByDoctor(RegisterPatientByDoctorRequest request, Doctor doctor) {
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

        String token = jwtTokenProvider.createToken(Map.of(
                "user_id", user.getId(),
                "role", user.getRole().name()
        ));

        Map<String, Object> userMap = new LinkedHashMap<>();
        userMap.put("id", user.getId());
        userMap.put("role", user.getRole().name());
        userMap.put("profile_id", patient.getId());

        return new TokenResponse(token, userMap);
    }
}