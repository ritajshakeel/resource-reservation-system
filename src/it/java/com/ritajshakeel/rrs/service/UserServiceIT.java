package com.ritajshakeel.rrs.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.persistence.JpaUserTransactionManager;

public class UserServiceIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;
    private static UserService service;

    @BeforeClass
    public static void setUpClass() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("hibernate.connection.url", postgres.getJdbcUrl());
        overrides.put("hibernate.connection.username", postgres.getUsername());
        overrides.put("hibernate.connection.password", postgres.getPassword());
        overrides.put("hibernate.connection.driver_class", "org.postgresql.Driver");

        entityManagerFactory = Persistence.createEntityManagerFactory("rrs", overrides);
        service = new UserService(new JpaUserTransactionManager(entityManagerFactory));
    }

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Test
    public void testRegisterPersistsUserWithGeneratedId() {
        User saved = service.register("Alice");

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    public void testListAllReturnsRegisteredUser() {
        service.register("Bob");

        List<User> users = service.listAll();

        assertThat(users).extracting(User::getName).contains("Bob");
    }
}