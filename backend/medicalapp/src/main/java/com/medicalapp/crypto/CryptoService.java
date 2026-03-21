package com.medicalapp.crypto;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CryptoService {

    private static final String PREFIX = "enc:v1:";
    private static final int IV_LEN = 12;          // 96-bit recommended for GCM
    private static final int TAG_LEN_BITS = 128;   // 16 bytes tag
    private final SecureRandom random = new SecureRandom();

    @Value("${app.crypto.key:}")
    private String base64Key;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("APP_CRYPTO_KEY is not set (app.crypto.key)");
        }
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("APP_CRYPTO_KEY must be base64 of 32 bytes (256-bit). Got: " + keyBytes.length);
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");

        CryptoHolder.set(this);
    }

    public String encrypt(String plain) {
        if (plain == null) return null;
        if (plain.startsWith(PREFIX)) return plain;

        try {
            byte[] iv = new byte[IV_LEN];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LEN_BITS, iv));

            byte[] ciphertext = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

            byte[] out = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ciphertext, 0, out, iv.length, ciphertext.length);

            return PREFIX + Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    public String decrypt(String value) {
        if (value == null) return null;
        if (!value.startsWith(PREFIX)) return value;

        try {
            String b64 = value.substring(PREFIX.length());
            byte[] raw = Base64.getDecoder().decode(b64);

            byte[] iv = new byte[IV_LEN];
            byte[] ciphertext = new byte[raw.length - IV_LEN];

            System.arraycopy(raw, 0, iv, 0, IV_LEN);
            System.arraycopy(raw, IV_LEN, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LEN_BITS, iv));

            byte[] plainBytes = cipher.doFinal(ciphertext);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }
}