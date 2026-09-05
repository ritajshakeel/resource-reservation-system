package com.ritajshakeel.rrs.view;

import java.util.List;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;

public interface RRSView {
    void userRegistered(User user);
    void usersListed(List<User> users);
    void resourceRegistered(Resource resource);
    void resourcesListed(List<Resource> resources);
    void reservationBooked(Reservation reservation);
    void reservationsListed(List<Reservation> reservations);
    void reservationConfirmed(Reservation reservation);
    void reservationCancelled(Reservation reservation);
    void showBookingError(String message);
    void showRegistrationError(String message);
    void showResourceRegistrationError(String message);
	void showReservationActionError(String message);
}