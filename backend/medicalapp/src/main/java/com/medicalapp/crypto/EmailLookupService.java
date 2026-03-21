package com.medicalapp.crypto;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EmailLookupService {

    @Value("${app.crypto.email-hmac-key:}")
    private String base64Key;

    private SecretKeySpec keySpec;

    @PostConstruct
    public void init() {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("APP_EMAIL_HMAC_KEY is not set (app.crypto.email-hmac-key)");
        }
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("APP_EMAIL_HMAC_KEY must be base64 of 32 bytes (256-bit). Got: " + keyBytes.length);
        }
        this.keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public String lookup(String email) {
        String norm = normalize(email);
        if (norm == null || norm.isBlank()) return null;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            byte[] h = mac.doFinal(norm.getBytes(StandardCharsets.UTF_8));
            return toHex(h);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC lookup failed", e);
        }
    }

    private static String toHex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        char[] alphabet = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex[i * 2] = alphabet[v >>> 4];
            hex[i * 2 + 1] = alphabet[v & 0x0F];
        }
        return new String(hex);
    }
}