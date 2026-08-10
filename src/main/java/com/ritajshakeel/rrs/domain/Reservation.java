package com.ritajshakeel.rrs.domain;

import java.time.LocalDateTime;

public class Reservation {
    private final User user;
    private final Resource resource;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public Reservation(User user, Resource resource, LocalDateTime start, LocalDateTime end) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (resource == null) {
            throw new IllegalArgumentException("Resource must not be null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.user = user;
        this.resource = resource;
        this.start = start;
        this.end = end;
    }
}