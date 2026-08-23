package com.ritajshakeel.rrs.repository.jpa;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.ritajshakeel.rrs.domain.User;

public class JpaUserRepositoryIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private JpaUserRepository repository;

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
        repository = new JpaUserRepository(entityManager);
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @Test
    public void testSavedUserIsReturnedByFindAll() {
        entityManager.getTransaction().begin();
        repository.save(new User("Alice"));
        entityManager.getTransaction().commit();

        List<User> users = repository.findAll();

        assertThat(users).extracting(User::getName).contains("Alice");
    }

    @Test
    public void testFindAllReturnsMultipleSavedUsers() {
        entityManager.getTransaction().begin();
        repository.save(new User("Bob"));
        repository.save(new User("Carol"));
        entityManager.getTransaction().commit();

        List<User> users = repository.findAll();

        assertThat(users).extracting(User::getName).contains("Bob", "Carol");
    }
}