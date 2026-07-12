package com.ritajshakeel.rrs.domain;

import java.time.LocalDateTime;

public class Reservation {
	private final LocalDateTime start;
    private final LocalDateTime end;

    public Reservation(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.start = start;
        this.end = end;
    }
}
