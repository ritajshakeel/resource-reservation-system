package com.ritajshakeel.rrs.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;

public class JpaReservationRepositoryIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private JpaReservationRepository repository;

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
        repository = new JpaReservationRepository(entityManager);
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    private Resource persistResource(String name) {
        entityManager.getTransaction().begin();
        Resource resource = new Resource(name);
        entityManager.persist(resource);
        entityManager.getTransaction().commit();
        return resource;
    }

    private User persistUser(String name) {
        entityManager.getTransaction().begin();
        User user = new User(name);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        return user;
    }

    @Test
    public void testNoOverlapWhenNoExistingReservations() {
        Resource resource = persistResource("Meeting Room A");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        boolean overlaps = repository.existsOverlapping(resource, start, end);

        assertThat(overlaps).isFalse();
    }

    @Test
    public void testOverlapDetectedForPendingReservation() {
        Resource resource = persistResource("Meeting Room B");
        User user = persistUser("Alice");
        LocalDateTime existingStart = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime existingEnd = LocalDateTime.of(2026, 7, 12, 10, 0);
        entityManager.getTransaction().begin();
        repository.save(new Reservation(user, resource, existingStart, existingEnd));
        entityManager.getTransaction().commit();

        LocalDateTime newStart = LocalDateTime.of(2026, 7, 12, 9, 30);
        LocalDateTime newEnd = LocalDateTime.of(2026, 7, 12, 10, 30);

        boolean overlaps = repository.existsOverlapping(resource, newStart, newEnd);

        assertThat(overlaps).isTrue();
    }

    @Test
    public void testBackToBackReservationsDoNotOverlap() {
        Resource resource = persistResource("Meeting Room C");
        User user = persistUser("Bob");
        LocalDateTime existingStart = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime existingEnd = LocalDateTime.of(2026, 7, 12, 10, 0);
        entityManager.getTransaction().begin();
        repository.save(new Reservation(user, resource, existingStart, existingEnd));
        entityManager.getTransaction().commit();

        LocalDateTime newStart = LocalDateTime.of(2026, 7, 12, 10, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 7, 12, 11, 0);

        boolean overlaps = repository.existsOverlapping(resource, newStart, newEnd);

        assertThat(overlaps).isFalse();
    }

    @Test
    public void testCancelledReservationDoesNotBlockSlot() {
        Resource resource = persistResource("Meeting Room D");
        User user = persistUser("Carol");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);
        Reservation reservation = new Reservation(user, resource, start, end);
        reservation.cancel();
        entityManager.getTransaction().begin();
        repository.save(reservation);
        entityManager.getTransaction().commit();

        boolean overlaps = repository.existsOverlapping(resource, start, end);

        assertThat(overlaps).isFalse();
    }
    
    @Test
    public void testFindByUserReturnsOnlyThatUsersReservations() {
        Resource resource = persistResource("Meeting Room E");
        User alice = persistUser("Alice");
        User bob = persistUser("Bob");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        entityManager.getTransaction().begin();
        repository.save(new Reservation(alice, resource, start, end));
        repository.save(new Reservation(bob, resource, start.plusHours(2), end.plusHours(2)));
        entityManager.getTransaction().commit();

        List<Reservation> aliceReservations = repository.findByUser(alice);

        assertThat(aliceReservations).hasSize(1);
        assertThat(aliceReservations.get(0).getUser()).isEqualTo(alice);
    }
}