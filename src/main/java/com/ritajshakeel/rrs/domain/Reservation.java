package com.ritajshakeel.rrs.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Reservation {
	
	private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Resource resource;

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "end_time")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
        // required by JPA/Hibernate
    }

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

    public Long getId() {
        return id;
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
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled reservation");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a cancelled reservation");
        }
        this.status = ReservationStatus.CANCELLED;
    }
    
    @Override
    public String toString() {
        return resource + ": " + start.format(DISPLAY_FORMAT) + " - " + end.format(DISPLAY_FORMAT) + " (" + status + ")";
    }
}