package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorProfileResponse {
    private Integer id;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String specialization;
    private String licenceNumber;
    private String role;
}