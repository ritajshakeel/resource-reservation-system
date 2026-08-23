package com.ritajshakeel.rrs.service;

import java.time.LocalDateTime;

import com.ritajshakeel.rrs.domain.Resource;

public class OverlappingReservationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OverlappingReservationException(Resource resource, LocalDateTime start, LocalDateTime end) {
        super("Resource " + resource.getName() + " is already booked between " + start + " and " + end);
    }
}