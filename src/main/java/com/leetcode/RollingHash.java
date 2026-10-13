package com.leetcode;

public final class RollingHash {

    private static final int DEFAULT_BASE = 26;
    private static final int DEFAULT_MODULUS = 181_081;

    private final int base;
    private final int modulus;
    private long hash;
    private long placeValue = 1;

    public RollingHash() {
        this(DEFAULT_BASE, DEFAULT_MODULUS);
    }

    public RollingHash(int base, int modulus) {
        if (base < 2) {
            throw new IllegalArgumentException("Base must be at least 2");
        }
        if (modulus < 2) {
            throw new IllegalArgumentException("Modulus must be at least 2");
        }

        this.base = base;
        this.modulus = modulus;
    }

    public void append(char character) {
        hash = (hash * base + valueOf(character)) % modulus;
    }

    public void prepend(char character) {
        hash = (valueOf(character) * placeValue + hash) % modulus;
        placeValue = (placeValue * base) % modulus;
    }

    public int value() {
        return (int) hash;
    }

    public void reset() {
        hash = 0;
        placeValue = 1;
    }

    private int valueOf(char character) {
        if (character < 'a' || character > 'z') {
            throw new IllegalArgumentException("RollingHash supports lowercase English letters only");
        }
        return character - 'a' + 1;
    }
}