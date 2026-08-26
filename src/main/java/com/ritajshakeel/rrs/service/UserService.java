package com.ritajshakeel.rrs.service;

import java.util.List;

import com.google.inject.Inject;

import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.persistence.UserTransactionManager;

public class UserService {

    private final UserTransactionManager transactionManager;

    @Inject
    public UserService(UserTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public User register(String name) {
        return transactionManager.doInTransaction(repository -> repository.save(new User(name)));
    }

    public List<User> listAll() {
        return transactionManager.doInTransaction(repository -> repository.findAll());
    }
}