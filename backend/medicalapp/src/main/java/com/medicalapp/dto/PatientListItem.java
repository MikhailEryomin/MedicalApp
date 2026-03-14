package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientListItem {
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
}