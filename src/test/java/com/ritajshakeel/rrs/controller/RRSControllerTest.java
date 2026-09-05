package com.ritajshakeel.rrs.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.service.OverlappingReservationException;
import com.ritajshakeel.rrs.service.ReservationService;
import com.ritajshakeel.rrs.service.ResourceService;
import com.ritajshakeel.rrs.service.UserService;
import com.ritajshakeel.rrs.view.RRSView;

public class RRSControllerTest {

    private RRSView view;
    private UserService userService;
    private ResourceService resourceService;
    private ReservationService reservationService;
    private RRSController controller;

    @Before
    public void setUp() {
        view = mock(RRSView.class);
        userService = mock(UserService.class);
        resourceService = mock(ResourceService.class);
        reservationService = mock(ReservationService.class);
        controller = new RRSController(view, userService, resourceService, reservationService);
    }

    @Test
    public void testRegisterUserCallsServiceAndNotifiesView() {
        User user = new User("Alice");
        when(userService.register("Alice")).thenReturn(user);

        controller.registerUser("Alice");

        verify(view).userRegistered(user);
    }
    
    @Test
    public void testBookReservationCallsServiceAndNotifiesView() {
        User user = new User("Alice");
        Resource resource = new Resource("Meeting Room A");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
        Reservation reservation = mock(Reservation.class);
        when(reservationService.book(user, resource, start, end)).thenReturn(reservation);

        controller.bookReservation(user, resource, start, end);

        verify(view).reservationBooked(reservation);
    }
    
    @Test
    public void testBookReservationWithOverlapNotifiesViewOfError() {
        User user = new User("Alice");
        Resource resource = new Resource("Meeting Room A");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
        when(reservationService.book(user, resource, start, end))
            .thenThrow(new OverlappingReservationException(resource, start, end));

        controller.bookReservation(user, resource, start, end);

        verify(view).showError(org.mockito.ArgumentMatchers.anyString());
    }
    
    @Test
    public void testRegisterResourceCallsServiceAndNotifiesView() {
        Resource resource = new Resource("Meeting Room A");
        when(resourceService.register("Meeting Room A")).thenReturn(resource);

        controller.registerResource("Meeting Room A");

        verify(view).resourceRegistered(resource);
    }

    @Test
    public void testConfirmReservationCallsServiceAndNotifiesView() {
        Reservation reservation = mock(Reservation.class);
        when(reservationService.confirmReservation(5L)).thenReturn(reservation);

        controller.confirmReservation(5L);

        verify(view).reservationConfirmed(reservation);
    }

    @Test
    public void testCancelReservationCallsServiceAndNotifiesView() {
        Reservation reservation = mock(Reservation.class);
        when(reservationService.cancelReservation(5L)).thenReturn(reservation);

        controller.cancelReservation(5L);

        verify(view).reservationCancelled(reservation);
    }

    @Test
    public void testFindReservationsForUserCallsServiceAndNotifiesView() {
        User user = new User("Alice");
        List<Reservation> reservations = List.of(mock(Reservation.class));
        when(reservationService.findReservationsForUser(user)).thenReturn(reservations);

        controller.findReservationsForUser(user);

        verify(view).reservationsListed(reservations);
    }
    
    @Test
    public void testLoadResourcesFetchesAndNotifiesView() {
        Resource roomA = new Resource("Meeting Room A");
        Resource roomB = new Resource("Meeting Room B");
        when(resourceService.listAll()).thenReturn(List.of(roomA, roomB));

        controller.loadResources();

        verify(view).resourcesListed(List.of(roomA, roomB));
    }
    
    @Test
    public void testOnActingAsUserSelectedLoadsReservationsWhenUserPresent() {
        User user = new User("Alice");
        Reservation reservation = mock(Reservation.class);
        when(reservationService.findReservationsForUser(user)).thenReturn(List.of(reservation));

        controller.onActingAsUserSelected(user);

        verify(view).reservationsListed(List.of(reservation));
    }

    @Test
    public void testOnActingAsUserSelectedDoesNothingWhenUserIsNull() {
        controller.onActingAsUserSelected(null);

        verify(view, never()).reservationsListed(any());
    }
}