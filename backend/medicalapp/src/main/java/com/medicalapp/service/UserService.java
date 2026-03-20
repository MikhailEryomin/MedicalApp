// src/main/java/com/medicalapp/service/UserService.java
package com.medicalapp.service;

import com.medicalapp.entity.Doctor;
import com.medicalapp.entity.Patient;
import com.medicalapp.entity.User;
import com.medicalapp.entity.enums.UserRole;
import com.medicalapp.exception.AppException;
import com.medicalapp.repository.UserRepository;
import com.medicalapp.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getCurrentUser(UserPrincipal principal) {
        return userRepository.findByIdWithProfiles(principal.getUserId())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @Transactional(readOnly = true)
    public Doctor getCurrentDoctor(UserPrincipal principal) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.DOCTOR || user.getDoctor() == null) {
            throw new AppException(HttpStatus.FORBIDDEN, "Doctor access required");
        }
        return user.getDoctor();
    }

    @Transactional(readOnly = true)
    public Patient getCurrentPatient(UserPrincipal principal) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.PATIENT || user.getPatient() == null) {
            throw new AppException(HttpStatus.FORBIDDEN, "Patient access required");
        }
        return user.getPatient();
    }
}