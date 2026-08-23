package com.ritajshakeel.rrs.repository.jpa;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.repository.ResourceRepository;

public class JpaResourceRepository implements ResourceRepository {

    private final EntityManager entityManager;

    public JpaResourceRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Resource save(Resource resource) {
        entityManager.persist(resource);
        return resource;
    }

    @Override
    public List<Resource> findAll() {
        TypedQuery<Resource> query = entityManager.createQuery("SELECT r FROM Resource r", Resource.class);
        return query.getResultList();
    }
}