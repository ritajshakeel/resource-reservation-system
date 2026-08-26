package com.ritajshakeel.rrs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.ReservationStatus;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.persistence.ReservationTransactionManager;
import com.ritajshakeel.rrs.repository.ReservationRepository;

public class ReservationServiceTest {

    private ReservationRepository repository;
    private ReservationTransactionManager transactionManager;
    private ReservationService service;

    @Before
    public void setUp() {
        repository = mock(ReservationRepository.class);
        transactionManager = mock(ReservationTransactionManager.class);
        when(transactionManager.doInTransaction(any())).thenAnswer(invocation -> {
            Function<ReservationRepository, ?> code = invocation.getArgument(0);
            return code.apply(repository);
        });
        service = new ReservationService(transactionManager);
    }

    @Test
    public void testBookingWithNoOverlapSavesReservationInASingleTransaction() {
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
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
        verify(transactionManager, times(1)).doInTransaction(any());
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

        verify(repository, never()).save(any());
        verify(transactionManager, times(1)).doInTransaction(any());
    }

    @Test
    public void testConfirmReservationFetchesAndConfirmsWithoutExplicitSave() {
        Reservation reservation = mock(Reservation.class);
        when(repository.findById(5L)).thenReturn(reservation);

        service.confirmReservation(5L);

        verify(reservation).confirm();
        verify(repository, never()).save(any());
    }

    @Test
    public void testCancelReservationFetchesAndCancelsWithoutExplicitSave() {
        Reservation reservation = mock(Reservation.class);
        when(repository.findById(5L)).thenReturn(reservation);

        service.cancelReservation(5L);

        verify(reservation).cancel();
        verify(repository, never()).save(any());
    }
}