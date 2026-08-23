package com.ritajshakeel.rrs.service;

import java.time.LocalDateTime;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.persistence.TransactionManager;

public class ReservationService {

    private final TransactionManager transactionManager;

    public ReservationService(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Reservation book(User user, Resource resource, LocalDateTime start, LocalDateTime end) {
        return transactionManager.doInTransaction(repository -> {
            if (repository.existsOverlapping(resource, start, end)) {
                throw new OverlappingReservationException(resource, start, end);
            }
            return repository.save(new Reservation(user, resource, start, end));
        });
    }
}