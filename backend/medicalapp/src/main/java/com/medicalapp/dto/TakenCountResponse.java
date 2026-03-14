package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TakenCountResponse {
    private Integer prescriptionId;
    private Integer takenCount;
    private Integer totalCount;
    private Boolean completed;
}