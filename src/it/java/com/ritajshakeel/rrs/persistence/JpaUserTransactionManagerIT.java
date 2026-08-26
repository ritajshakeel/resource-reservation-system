package com.ritajshakeel.rrs.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

import com.ritajshakeel.rrs.domain.User;

public class JpaUserTransactionManagerIT {

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
        JpaUserTransactionManager transactionManager = new JpaUserTransactionManager(entityManagerFactory);

        assertThatThrownBy(() -> transactionManager.doInTransaction(repository -> {
            repository.save(new User("ShouldNotPersist"));
            throw new RuntimeException("Forced failure");
        })).isInstanceOf(RuntimeException.class).hasMessage("Forced failure");

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Long count = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.name = :name", Long.class)
                .setParameter("name", "ShouldNotPersist")
                .getSingleResult();
            assertThat(count).isEqualTo(0L);
        }
    }
}