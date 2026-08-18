package com.ritajshakeel.rrs.domain;

import java.time.LocalDateTime;

public class Reservation {
    private final User user;
    private final Resource resource;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private ReservationStatus status;

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
        this.status = ReservationStatus.PENDING;
    }

    public User getUser() {
        return user;
    }

    public Resource getResource() {
        return resource;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }
}