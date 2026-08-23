package com.ritajshakeel.rrs.repository;

import java.time.LocalDateTime;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    boolean existsOverlapping(Resource resource, LocalDateTime start, LocalDateTime end);
}