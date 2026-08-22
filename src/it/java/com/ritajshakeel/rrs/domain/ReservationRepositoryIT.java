package com.ritajshakeel.rrs.domain;

import static org.assertj.core.api.Assertions.assertThat;

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

public class ReservationRepositoryIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

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
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @Test
    public void testSavingAndRetrievingReservation() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        entityManager.getTransaction().begin();
        User user = new User("Alice");
        Resource resource = new Resource("Meeting Room A");
        entityManager.persist(user);
        entityManager.persist(resource);

        Reservation reservation = new Reservation(user, resource, start, end);
        entityManager.persist(reservation);
        entityManager.getTransaction().commit();

        Reservation found = entityManager.find(Reservation.class, reservation.getId());

        assertThat(found.getUser().getName()).isEqualTo("Alice");
        assertThat(found.getResource().getName()).isEqualTo("Meeting Room A");
        assertThat(found.getStart()).isEqualTo(start);
        assertThat(found.getEnd()).isEqualTo(end);
        assertThat(found.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }
}