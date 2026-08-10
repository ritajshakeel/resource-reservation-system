package com.ritajshakeel.rrs.domain;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;
import org.junit.Test;

public class ReservationTest {
	@Test
	public void testCreatingReservationWithEndBeforeStartThrowsException() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 9, 0);

        assertThatThrownBy(() -> new Reservation(start, end))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("End time must be after start time");
    }
	
}
