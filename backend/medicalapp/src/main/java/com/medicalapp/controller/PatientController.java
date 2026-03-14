package com.medicalapp.controller;

import com.medicalapp.dto.*;
import com.medicalapp.entity.Doctor;
import com.medicalapp.entity.User;
import com.medicalapp.security.UserPrincipal;
import com.medicalapp.service.PatientService;
import com.medicalapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Patients")
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;

    @GetMapping("/api/doctors/me/patients")
    public ResponseEntity<List<PatientListItem>> getMyPatients(
            @AuthenticationPrincipal UserPrincipal principal) {
        Doctor doctor = userService.getCurrentDoctor(principal);
        return ResponseEntity.ok(patientService.getMyPatients(doctor));
    }

    @GetMapping("/api/patients/{patientId}")
    public ResponseEntity<PatientDetailResponse> getPatientDetail(
            @PathVariable Integer patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = userService.getCurrentUser(principal);
        return ResponseEntity.ok(patientService.getPatientDetail(patientId, currentUser));
    }

    @PostMapping("/api/patients")
    public ResponseEntity<PatientDetailResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Doctor doctor = userService.getCurrentDoctor(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(request, doctor));
    }
}