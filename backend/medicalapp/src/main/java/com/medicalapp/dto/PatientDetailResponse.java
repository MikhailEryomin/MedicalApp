package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PatientDetailResponse {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private List<String> allergies;
    private List<String> chronicDiseases;
}