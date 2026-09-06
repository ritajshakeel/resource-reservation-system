package com.ritajshakeel.rrs.guice;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import com.ritajshakeel.rrs.controller.RRSController;
import com.ritajshakeel.rrs.controller.RRSControllerFactory;
import com.ritajshakeel.rrs.persistence.JpaReservationTransactionManager;
import com.ritajshakeel.rrs.persistence.JpaUserTransactionManager;
import com.ritajshakeel.rrs.persistence.ResourceTransactionManager;
import com.ritajshakeel.rrs.persistence.ReservationTransactionManager;
import com.ritajshakeel.rrs.persistence.UserTransactionManager;
import com.ritajshakeel.rrs.persistence.JpaResourceTransactionManager;
import com.ritajshakeel.rrs.view.swing.RRSSwingView;

public class RRSModule extends AbstractModule {

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public RRSModule() {
        Properties defaults = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/db.properties")) {
            defaults.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load db.properties", e);
        }
        this.dbUrl = defaults.getProperty("db.url");
        this.dbUsername = defaults.getProperty("db.username");
        this.dbPassword = defaults.getProperty("db.password");
    }

    public RRSModule dbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
        return this;
    }

    public RRSModule dbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
        return this;
    }

    public RRSModule dbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }

    @Override
    protected void configure() {
        bind(ReservationTransactionManager.class).to(JpaReservationTransactionManager.class);
        bind(UserTransactionManager.class).to(JpaUserTransactionManager.class);
        bind(ResourceTransactionManager.class).to(JpaResourceTransactionManager.class);
        install(new FactoryModuleBuilder()
                .implement(RRSController.class, RRSController.class)
                .build(RRSControllerFactory.class));
    }

    @Provides
    @Singleton
    EntityManagerFactory entityManagerFactory() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("hibernate.connection.url", dbUrl);
        overrides.put("hibernate.connection.username", dbUsername);
        overrides.put("hibernate.connection.password", dbPassword);
        overrides.put("hibernate.connection.driver_class", "org.postgresql.Driver");

        return Persistence.createEntityManagerFactory("rrs", overrides);
    }

    @Provides
    @Singleton
    RRSSwingView rrsSwingView(RRSControllerFactory controllerFactory) {
        RRSSwingView view = new RRSSwingView();
        view.setController(controllerFactory.create(view));
        return view;
    }
}