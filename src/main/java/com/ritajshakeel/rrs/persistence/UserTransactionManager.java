package com.ritajshakeel.rrs.persistence;

import java.util.function.Function;

import com.ritajshakeel.rrs.repository.UserRepository;

public interface UserTransactionManager {
    <T> T doInTransaction(Function<UserRepository, T> code);
}