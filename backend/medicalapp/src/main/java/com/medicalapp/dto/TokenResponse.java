package com.medicalapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String token;
    private Map<String, Object> user;
}