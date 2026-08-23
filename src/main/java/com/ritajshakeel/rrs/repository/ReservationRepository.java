package com.ritajshakeel.rrs.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    boolean existsOverlapping(Resource resource, LocalDateTime start, LocalDateTime end);
    
    List<Reservation> findByUser(User user);
    
    Reservation findById(Long id);
}