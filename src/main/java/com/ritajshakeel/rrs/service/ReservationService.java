package com.ritajshakeel.rrs.service;

import java.time.LocalDateTime;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.repository.ReservationRepository;

public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation book(User user, Resource resource, LocalDateTime start, LocalDateTime end) {
        if (repository.existsOverlapping(resource, start, end)) {
            throw new OverlappingReservationException(resource, start, end);
        }
        return repository.save(new Reservation(user, resource, start, end));
    }
}