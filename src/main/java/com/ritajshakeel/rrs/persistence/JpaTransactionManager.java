package com.ritajshakeel.rrs.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import com.ritajshakeel.rrs.repository.ReservationRepository;
import com.ritajshakeel.rrs.repository.jpa.JpaReservationRepository;

public class JpaTransactionManager implements TransactionManager {

    private final EntityManagerFactory entityManagerFactory;

    public JpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <T> T doInTransaction(TransactionCode<T> code) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            ReservationRepository repository = new JpaReservationRepository(entityManager);
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