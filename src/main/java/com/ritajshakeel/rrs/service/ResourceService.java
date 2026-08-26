package com.ritajshakeel.rrs.service;

import java.util.List;

import com.google.inject.Inject;

import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.persistence.ResourceTransactionManager;

public class ResourceService {

    private final ResourceTransactionManager transactionManager;

    @Inject
    public ResourceService(ResourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Resource register(String name) {
        return transactionManager.doInTransaction(repository -> repository.save(new Resource(name)));
    }

    public List<Resource> listAll() {
        return transactionManager.doInTransaction(repository -> repository.findAll());
    }
}