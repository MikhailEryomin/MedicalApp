package com.medicalapp.controller;

import com.medicalapp.dto.*;
import com.medicalapp.entity.Doctor;
import com.medicalapp.entity.User;
import com.medicalapp.security.UserPrincipal;
import com.medicalapp.service.PrescriptionService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final UserService userService;

    @PostMapping("/prescriptions")
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Doctor doctor = userService.getCurrentDoctor(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.create(request, doctor));
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<List<PrescriptionResponse>> getPatientPrescriptions(
            @PathVariable Integer patientId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = userService.getCurrentUser(principal);
        return ResponseEntity.ok(prescriptionService.getPatientPrescriptions(patientId, currentUser));
    }

    @PostMapping("/prescriptions/{prescriptionId}/log")
    public ResponseEntity<PrescriptionLogResponse> markAsTaken(
            @PathVariable Integer prescriptionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = userService.getCurrentUser(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.markAsTaken(prescriptionId, currentUser));
    }

    @GetMapping("/prescriptions/{prescriptionId}/taken-count")
    public ResponseEntity<TakenCountResponse> getTakenCount(
            @PathVariable Integer prescriptionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        User currentUser = userService.getCurrentUser(principal);
        return ResponseEntity.ok(prescriptionService.getTakenCountInfo(prescriptionId, currentUser));
    }
}