package com.ritajshakeel.rrs.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.ReservationStatus;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.repository.ReservationRepository;

public class JpaReservationRepository implements ReservationRepository {

    private final EntityManager entityManager;

    public JpaReservationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Reservation save(Reservation reservation) {
        entityManager.getTransaction().begin();
        entityManager.persist(reservation);
        entityManager.getTransaction().commit();
        return reservation;
    }

    @Override
    public boolean existsOverlapping(Resource resource, LocalDateTime start, LocalDateTime end) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.resource = :resource " +
            "AND r.status IN :activeStatuses " +
            "AND r.start < :end AND :start < r.end",
            Long.class);
        query.setParameter("resource", resource);
        query.setParameter("activeStatuses", List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));
        query.setParameter("start", start);
        query.setParameter("end", end);

        return query.getSingleResult() > 0;
    }
}