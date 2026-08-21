package com.ritajshakeel.rrs.domain;

public class User {
    private final String name;

    public User(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        this.name = name;
    }

	public String getName() {
		return name;
	}
}