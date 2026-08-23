package com.ritajshakeel.rrs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.ReservationStatus;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.repository.ReservationRepository;

public class ReservationServiceTest {

    private ReservationRepository repository;
    private ReservationService service;

    @Before
    public void setUp() {
        repository = mock(ReservationRepository.class);
        service = new ReservationService(repository);
    }

    @Test
    public void testBookingWithNoOverlapSavesReservation() {
        User user = new User("Alice");
        Resource resource = new Resource("Meeting Room A");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        when(repository.existsOverlapping(resource, start, end)).thenReturn(false);

        service.book(user, resource, start, end);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(repository).save(captor.capture());
        Reservation saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getResource()).isEqualTo(resource);
        assertThat(saved.getStart()).isEqualTo(start);
        assertThat(saved.getEnd()).isEqualTo(end);
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    public void testBookingWithOverlapThrowsExceptionAndNeverSaves() {
        User user = new User("Bob");
        Resource resource = new Resource("Meeting Room B");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        when(repository.existsOverlapping(resource, start, end)).thenReturn(true);

        assertThatThrownBy(() -> service.book(user, resource, start, end))
            .isInstanceOf(OverlappingReservationException.class);

        verify(repository, never()).save(org.mockito.Mockito.any());
    }
}