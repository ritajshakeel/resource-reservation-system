package com.ritajshakeel.rrs.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.service.OverlappingReservationException;
import com.ritajshakeel.rrs.service.ReservationService;
import com.ritajshakeel.rrs.service.ResourceService;
import com.ritajshakeel.rrs.service.UserService;
import com.ritajshakeel.rrs.view.RRSView;

public class RRSController {

    private final RRSView view;
    private final UserService userService;
    private final ResourceService resourceService;
    private final ReservationService reservationService;

    @Inject
    public RRSController(@Assisted RRSView view, UserService userService,
            ResourceService resourceService, ReservationService reservationService) {
        this.view = view;
        this.userService = userService;
        this.resourceService = resourceService;
        this.reservationService = reservationService;
    }

    public void registerUser(String name) {
        try {
            User user = userService.register(name);
            view.userRegistered(user);
        } catch (IllegalArgumentException e) {
            view.showRegistrationError(e.getMessage());
        }
    }

    public void bookReservation(User user, Resource resource, LocalDateTime start, LocalDateTime end) {
        try {
            Reservation reservation = reservationService.book(user, resource, start, end);
            view.reservationBooked(reservation);
        } catch (OverlappingReservationException | IllegalArgumentException e) {
            view.showBookingError(e.getMessage());
        }
    }
    
    public void registerResource(String name) {
        try {
            Resource resource = resourceService.register(name);
            view.resourceRegistered(resource);
        } catch (IllegalArgumentException e) {
            view.showResourceRegistrationError(e.getMessage());
        }
    }

    public void confirmReservation(Long reservationId) {
        try {
            Reservation reservation = reservationService.confirmReservation(reservationId);
            view.reservationConfirmed(reservation);
        } catch (IllegalStateException e) {
            view.showReservationActionError(e.getMessage());
        }
    }

    public void cancelReservation(Long reservationId) {
        try {
            Reservation reservation = reservationService.cancelReservation(reservationId);
            view.reservationCancelled(reservation);
        } catch (IllegalStateException e) {
            view.showReservationActionError(e.getMessage());
        }
    }

    public void findReservationsForUser(User user) {
        List<Reservation> reservations = reservationService.findReservationsForUser(user);
        view.reservationsListed(reservations);
    }
    
    public void loadResources() {
        List<Resource> resources = resourceService.listAll();
        view.resourcesListed(resources);
    }
    
    public void onActingAsUserSelected(User user) {
        if (user != null) {
            findReservationsForUser(user);
        }
    }
    
    public void loadUsers() {
        List<User> users = userService.listAll();
        view.usersListed(users);
    }
}