package com.ritajshakeel.rrs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.ReservationStatus;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.persistence.JpaReservationTransactionManager;

public class ReservationServiceIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private ReservationService service;

    @BeforeClass
    public static void setUpClass() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("hibernate.connection.url", postgres.getJdbcUrl());
        overrides.put("hibernate.connection.username", postgres.getUsername());
        overrides.put("hibernate.connection.password", postgres.getPassword());
        overrides.put("hibernate.connection.driver_class", "org.postgresql.Driver");

        entityManagerFactory = Persistence.createEntityManagerFactory("rrs", overrides);
    }

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Before
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        service = new ReservationService(new JpaReservationTransactionManager(entityManagerFactory));
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    private User persistUser(String name) {
        entityManager.getTransaction().begin();
        User user = new User(name);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        return user;
    }

    private Resource persistResource(String name) {
        entityManager.getTransaction().begin();
        Resource resource = new Resource(name);
        entityManager.persist(resource);
        entityManager.getTransaction().commit();
        return resource;
    }

    @Test
    public void testBookingReservationEndToEnd() {
        User user = persistUser("Alice");
        Resource resource = persistResource("Meeting Room A");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        Reservation saved = service.book(user, resource, start, end);

        Reservation found = entityManager.find(Reservation.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUser().getName()).isEqualTo("Alice");
    }

    @Test
    public void testBookingOverlappingReservationIsRejectedAndNotPersisted() {
        User user = persistUser("Bob");
        Resource resource = persistResource("Meeting Room B");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        service.book(user, resource, start, end);

        assertThatThrownBy(() -> service.book(user, resource, start, end))
            .isInstanceOf(OverlappingReservationException.class);

        Long count = entityManager.createQuery(
                "SELECT COUNT(r) FROM Reservation r WHERE r.resource = :resource", Long.class)
            .setParameter("resource", resource)
            .getSingleResult();
        assertThat(count).isEqualTo(1L);
    }
    
    @Test
    public void testConfirmingReservationPersistsStatusChangeViaDirtyChecking() {
        User user = persistUser("Dave");
        Resource resource = persistResource("Meeting Room F");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
        Reservation saved = service.book(user, resource, start, end);

        service.confirmReservation(saved.getId());

        Reservation found = entityManager.find(Reservation.class, saved.getId());
        assertThat(found.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }
}