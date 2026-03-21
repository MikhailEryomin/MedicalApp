package com.medicalapp.crypto;

public final class CryptoHolder {
    private static CryptoService crypto;

    private CryptoHolder() {}

    public static void set(CryptoService service) {
        crypto = service;
    }

    public static CryptoService get() {
        if (crypto == null) {
            throw new IllegalStateException("CryptoService not initialized");
        }
        return crypto;
    }
}