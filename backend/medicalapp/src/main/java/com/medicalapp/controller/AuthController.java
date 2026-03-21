package com.medicalapp.controller;

import com.medicalapp.dto.*;
import com.medicalapp.entity.Doctor;
import com.medicalapp.security.UserPrincipal;
import com.medicalapp.service.AuthService;
import com.medicalapp.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.medicalapp.dto.RegisterPatientRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<TokenResponse> registerDoctor(@Valid @RequestBody RegisterDoctorRequest request) {
        return ResponseEntity.ok(authService.registerDoctor(request));
    }

//    @PostMapping("/register")
//    public ResponseEntity<TokenResponse> registerPatientByDoctor(
//            @Valid @RequestBody RegisterPatientByDoctorRequest request,
//            @AuthenticationPrincipal UserPrincipal principal) {
//        Doctor doctor = userService.getCurrentDoctor(principal);
//        return ResponseEntity.ok(authService.registerPatientByDoctor(request, doctor));
//    }

    @PostMapping("/register/patient")
    public ResponseEntity<TokenResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.ok(authService.registerPatient(request));
    }
}