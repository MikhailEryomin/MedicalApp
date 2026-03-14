package com.medicalapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePrescriptionRequest {
    @NotNull
    private Integer patientId;
    @NotNull
    private Integer medicineId;
    @NotBlank
    private String dosage;
    @NotNull @Min(1)
    private Integer frequency;
    @NotNull @Min(1)
    private Integer durationDays;
    private String notes;
}