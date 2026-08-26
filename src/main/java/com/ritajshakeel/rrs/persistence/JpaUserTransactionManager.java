package com.ritajshakeel.rrs.persistence;

import java.util.function.Function;

import com.google.inject.Inject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import com.ritajshakeel.rrs.repository.UserRepository;
import com.ritajshakeel.rrs.repository.jpa.JpaUserRepository;

public class JpaUserTransactionManager implements UserTransactionManager {

    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public JpaUserTransactionManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <T> T doInTransaction(Function<UserRepository, T> code) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            UserRepository repository = new JpaUserRepository(entityManager);
            T result = code.apply(repository);
            entityManager.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }
}