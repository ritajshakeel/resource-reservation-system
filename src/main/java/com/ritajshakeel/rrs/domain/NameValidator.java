package com.ritajshakeel.rrs.domain;

final class NameValidator {

    private NameValidator() {
    }

    static void validate(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
    }
}