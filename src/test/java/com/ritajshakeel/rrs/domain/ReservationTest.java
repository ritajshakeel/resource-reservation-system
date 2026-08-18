package com.ritajshakeel.rrs.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;
import org.junit.Test;

public class ReservationTest {

	@Test
	public void testCreatingReservationWithEndBeforeStartThrowsException() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 9, 0);
        User user = new User();
        Resource resource = new Resource();

        assertThatThrownBy(() -> new Reservation(user, resource, start, end))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("End time must be after start time");
    }

	@Test
	public void testCreatingReservationWithNullUserThrowsException() {
	    LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
	    LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
	    User user = null;
	    Resource resource = new Resource();

	    assertThatThrownBy(() -> new Reservation(user, resource, start, end))
	        .isInstanceOf(IllegalArgumentException.class)
	        .hasMessage("User must not be null");
	}
	
	@Test
	public void testCreatingReservationWithNullResourceThrowsException() {
	    LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
	    LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
	    User user = new User();
	    Resource resource = null;

	    assertThatThrownBy(() -> new Reservation(user, resource, start, end))
	        .isInstanceOf(IllegalArgumentException.class)
	        .hasMessage("Resource must not be null");
	}
	
	@Test
	public void testCreatingValidReservationStoresUserResourceAndTimes() {
	    LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
	    LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
	    User user = new User();
	    Resource resource = new Resource();

	    Reservation reservation = new Reservation(user, resource, start, end);

	    assertThat(reservation.getUser()).isEqualTo(user);
	    assertThat(reservation.getResource()).isEqualTo(resource);
	    assertThat(reservation.getStart()).isEqualTo(start);
	    assertThat(reservation.getEnd()).isEqualTo(end);
	}

}