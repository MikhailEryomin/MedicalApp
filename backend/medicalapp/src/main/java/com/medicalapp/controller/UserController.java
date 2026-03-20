package com.medicalapp.controller;

import com.medicalapp.dto.DoctorProfileResponse;
import com.medicalapp.dto.PatientProfileResponse;
import com.medicalapp.entity.User;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.security.UserPrincipal;
import com.medicalapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = userService.getCurrentUser(principal);

        if (currentUser.getRole() == UserRole.DOCTOR && currentUser.getDoctor() != null) {
            var d = currentUser.getDoctor();
            return ResponseEntity.ok(DoctorProfileResponse.builder()
                    .id(d.getId())
                    .userId(d.getUser().getId())
                    .firstName(d.getFirstName())
                    .lastName(d.getLastName())
                    .specialization(d.getSpecialization())
                    .licenceNumber(d.getLicenceNumber())
                    .role("DOCTOR")
                    .build());
        } else if (currentUser.getRole() == UserRole.PATIENT && currentUser.getPatient() != null) {
            var p = currentUser.getPatient();
            return ResponseEntity.ok(PatientProfileResponse.builder()
                    .id(p.getId())
                    .userId(p.getUser().getId())
                    .firstName(p.getFirstName())
                    .lastName(p.getLastName())
                    .birthDate(p.getBirthDate())
                    .gender(p.getGender() != null ? p.getGender().name() : null)
                    .allergies(p.getAllergies() != null ? p.getAllergies() : new ArrayList<>())
                    .chronicDiseases(p.getChronicDiseases() != null ? p.getChronicDiseases() : new ArrayList<>())
                    .role("PATIENT")
                    .build());
        }
        return ResponseEntity.ok(Map.of("detail", "Profile not configured"));
    }
}