package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PrescriptionLogResponse {
    private Integer id;
    private Integer prescriptionId;
    private LocalDateTime takenAt;
}