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
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
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

    @GetMapping("/api/patients")
    public ResponseEntity<List<PatientListItem>> getAllPatientsGlobal(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "200") int limit,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.getCurrentDoctor(principal);
        return ResponseEntity.ok(patientService.getAllPatientsGlobal(search, limit));
    }

    @DeleteMapping("/api/doctors/me/patients/{patientId}")
    public ResponseEntity<?> unassignPatientFromMe(
            @PathVariable Integer patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Doctor doctor = userService.getCurrentDoctor(principal);

        boolean removed = patientService.unassignPatientFromDoctor(patientId, doctor);

        if (removed) {
            return ResponseEntity.ok(Map.of("detail", "Patient unassigned"));
        }
        return ResponseEntity.ok(Map.of("detail", "Patient is not assigned to you"));
    }

    @PostMapping("/api/doctors/me/patients/{patientId}")
    public ResponseEntity<?> assignPatientToMe(
            @PathVariable Integer patientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Doctor doctor = userService.getCurrentDoctor(principal);
        boolean created = patientService.assignPatientToDoctor(patientId, doctor);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("detail", "Patient assigned to doctor"));
        }
        return ResponseEntity.ok(Map.of("detail", "Patient already assigned to doctor"));
    }
}