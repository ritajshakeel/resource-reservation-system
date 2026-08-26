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

import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.persistence.JpaResourceTransactionManager;

public class ResourceServiceIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static EntityManagerFactory entityManagerFactory;
    private static ResourceService service;

    @BeforeClass
    public static void setUpClass() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("hibernate.connection.url", postgres.getJdbcUrl());
        overrides.put("hibernate.connection.username", postgres.getUsername());
        overrides.put("hibernate.connection.password", postgres.getPassword());
        overrides.put("hibernate.connection.driver_class", "org.postgresql.Driver");

        entityManagerFactory = Persistence.createEntityManagerFactory("rrs", overrides);
        service = new ResourceService(new JpaResourceTransactionManager(entityManagerFactory));
    }

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Test
    public void testRegisterPersistsResourceWithGeneratedId() {
        Resource saved = service.register("Meeting Room A");

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    public void testListAllReturnsRegisteredResource() {
        service.register("Meeting Room B");

        List<Resource> resources = service.listAll();

        assertThat(resources).extracting(Resource::getName).contains("Meeting Room B");
    }
}