package com.ritajshakeel.rrs.persistence;

import java.util.function.Function;

import com.google.inject.Inject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import com.ritajshakeel.rrs.repository.ResourceRepository;
import com.ritajshakeel.rrs.repository.jpa.JpaResourceRepository;

public class JpaResourceTransactionManager implements ResourceTransactionManager {

    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public JpaResourceTransactionManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <T> T doInTransaction(Function<ResourceRepository, T> code) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            ResourceRepository repository = new JpaResourceRepository(entityManager);
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