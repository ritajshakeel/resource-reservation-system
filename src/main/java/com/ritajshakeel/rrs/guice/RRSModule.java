package com.ritajshakeel.rrs.guice;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import com.ritajshakeel.rrs.persistence.JpaTransactionManager;
import com.ritajshakeel.rrs.persistence.TransactionManager;

public class RRSModule extends AbstractModule {

    private String dbUrl = "jdbc:postgresql://localhost:5432/rrs";
    private String dbUsername = "postgres";
    private String dbPassword = "postgres";

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
        bind(TransactionManager.class).to(JpaTransactionManager.class);
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
}