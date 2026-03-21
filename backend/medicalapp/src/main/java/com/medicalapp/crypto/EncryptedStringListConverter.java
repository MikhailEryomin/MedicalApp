package com.medicalapp.crypto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class EncryptedStringListConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> LIST = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            List<String> safe = attribute != null ? attribute : new ArrayList<>();
            String json = MAPPER.writeValueAsString(safe);
            return CryptoHolder.get().encrypt(json);
        } catch (Exception e) {
            throw new IllegalStateException("List<String> serialize/encrypt failed", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            String json = CryptoHolder.get().decrypt(dbData);
            if (json == null || json.isBlank()) return new ArrayList<>();
            return MAPPER.readValue(json, LIST);
        } catch (Exception e) {
            throw new IllegalStateException("List<String> decrypt/deserialize failed", e);
        }
    }
}