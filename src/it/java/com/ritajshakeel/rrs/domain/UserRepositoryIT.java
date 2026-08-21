package com.ritajshakeel.rrs.domain;

import static org.assertj.core.api.Assertions.assertThat;

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

public class UserRepositoryIT {

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
    public void testSavingAndRetrievingUser() {
        entityManager.getTransaction().begin();
        User user = new User("Alice");
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        User found = entityManager.find(User.class, user.getId());

        assertThat(found.getName()).isEqualTo("Alice");
    }
}