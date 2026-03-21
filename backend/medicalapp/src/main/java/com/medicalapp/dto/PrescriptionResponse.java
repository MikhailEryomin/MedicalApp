package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PrescriptionResponse {
    private Integer id;
    private Integer doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private Integer patientId;
    private Integer medicineId;
    private String medicineName;
    private String dosage;
    private Integer frequency;
    private Integer durationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String notes;
    private Integer takenCount;
    private Integer totalCount;
}