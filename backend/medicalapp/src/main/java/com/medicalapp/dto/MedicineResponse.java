package com.medicalapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicineResponse {
    private Integer id;
    private String name;
    private String form;
    private String defaultDosage;
}