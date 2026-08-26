package com.ritajshakeel.rrs.guice;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.ritajshakeel.rrs.domain.Reservation;
import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.service.ReservationService;
import com.ritajshakeel.rrs.service.ResourceService;
import com.ritajshakeel.rrs.service.UserService;

public class RRSModuleIT {

    @ClassRule
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static Injector injector;
    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeClass
    public static void setUpClass() {
        injector = Guice.createInjector(
            new RRSModule()
                .dbUrl(postgres.getJdbcUrl())
                .dbUsername(postgres.getUsername())
                .dbPassword(postgres.getPassword()));

        entityManagerFactory = injector.getInstance(EntityManagerFactory.class);
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
    public void testGuiceWiredReservationServiceBooksReservationEndToEnd() {
        ReservationService service = injector.getInstance(ReservationService.class);
        User user = persistUser("Eve");
        Resource resource = persistResource("Meeting Room G");
        LocalDateTime start = LocalDateTime.of(2026, 7, 12, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 12, 10, 0);

        Reservation saved = service.book(user, resource, start, end);

        Reservation found = entityManager.find(Reservation.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUser().getName()).isEqualTo("Eve");
    }
    
    @Test
    public void testGuiceWiredUserServiceRegistersUserEndToEnd() {
        UserService userService = injector.getInstance(UserService.class);

        User saved = userService.register("Frank");

        User found = entityManager.find(User.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Frank");
    }

    @Test
    public void testGuiceWiredResourceServiceRegistersResourceEndToEnd() {
        ResourceService resourceService = injector.getInstance(ResourceService.class);

        Resource saved = resourceService.register("Meeting Room H");

        Resource found = entityManager.find(Resource.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Meeting Room H");
    }
}