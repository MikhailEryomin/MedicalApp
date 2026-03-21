package com.medicalapp.service;

import com.medicalapp.crypto.EmailLookupService;
import com.medicalapp.dto.LoginRequest;
import com.medicalapp.dto.RegisterDoctorRequest;
import com.medicalapp.dto.RegisterPatientRequest;
import com.medicalapp.dto.TokenResponse;
import com.medicalapp.entity.Doctor;
import com.medicalapp.entity.Patient;
import com.medicalapp.entity.User;
import com.medicalapp.entity.enums.Gender;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.exception.AppException;
import com.medicalapp.repository.DoctorRepository;
import com.medicalapp.repository.PatientRepository;
import com.medicalapp.repository.UserRepository;
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
    private final EmailLookupService emailLookupService;

    private Map<String, Object> buildUserMap(Integer id, String role, Integer profileId) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("role", role);
        map.put("profile_id", profileId); // может быть null (например если профиля нет)
        return map;
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        String lookup = emailLookupService.lookup(request.getEmail());
        if (lookup == null) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userRepository.findByEmailLookupWithProfiles(lookup)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!user.getRole().name().equals(request.getRole())) {
            throw new AppException(
                    HttpStatus.UNAUTHORIZED,
                    "This account is registered as " + user.getRole().name() + ", not " + request.getRole()
            );
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("role", user.getRole().name());
        String token = jwtTokenProvider.createToken(claims);

        Integer profileId = null;
        if (user.getRole() == UserRole.DOCTOR && user.getDoctor() != null) {
            profileId = user.getDoctor().getId();
        } else if (user.getRole() == UserRole.PATIENT && user.getPatient() != null) {
            profileId = user.getPatient().getId();
        }

        return new TokenResponse(token, buildUserMap(user.getId(), user.getRole().name(), profileId));
    }

    @Transactional
    public TokenResponse registerPatient(RegisterPatientRequest request) {
        String normalizedEmail = emailLookupService.normalize(request.getEmail());
        String lookup = emailLookupService.lookup(normalizedEmail);

        if (userRepository.existsByEmailLookup(lookup)) {
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
                .email(normalizedEmail)      // в БД уйдет в шифрованном виде
                .emailLookup(lookup)         // для поиска/уникальности
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

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("role", user.getRole().name());
        String token = jwtTokenProvider.createToken(claims);

        return new TokenResponse(token, buildUserMap(user.getId(), user.getRole().name(), patient.getId()));
    }

    @Transactional
    public TokenResponse registerDoctor(RegisterDoctorRequest request) {
        String normalizedEmail = emailLookupService.normalize(request.getEmail());
        String lookup = emailLookupService.lookup(normalizedEmail);

        if (userRepository.existsByEmailLookup(lookup)) {
            throw new AppException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = User.builder()
                .email(normalizedEmail)     // в БД уйдет в шифрованном виде
                .emailLookup(lookup)
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

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("role", user.getRole().name());
        String token = jwtTokenProvider.createToken(claims);

        return new TokenResponse(token, buildUserMap(user.getId(), user.getRole().name(), doctor.getId()));
    }
}