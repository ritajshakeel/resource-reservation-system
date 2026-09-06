package com.ritajshakeel.rrs.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;

public class JpaReservationTransactionManagerIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;

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

    @Test
    public void testFailureInsideTransactionRollsBackAndPropagatesException() {
        User user = persistUser("Alice");
        Resource resource = persistResource("Meeting Room Z");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        JpaReservationTransactionManager transactionManager =
            new JpaReservationTransactionManager(entityManagerFactory);

        assertThatThrownBy(() -> transactionManager.doInTransaction(repository -> {
            repository.save(new Reservation(user, resource, start, end));
            throw new RuntimeException("Forced failure");
        })).isInstanceOf(RuntimeException.class).hasMessage("Forced failure");

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Long count = entityManager.createQuery(
                    "SELECT COUNT(r) FROM Reservation r WHERE r.user = :user AND r.resource = :resource",
                    Long.class)
                .setParameter("user", user)
                .setParameter("resource", resource)
                .getSingleResult();
            assertThat(count).isZero();
        }
    }

    private User persistUser(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            User user = new User(name);
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            return user;
        } finally {
            entityManager.close();
        }
    }

    private Resource persistResource(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Resource resource = new Resource(name);
            entityManager.persist(resource);
            entityManager.getTransaction().commit();
            return resource;
        } finally {
            entityManager.close();
        }
    }
}