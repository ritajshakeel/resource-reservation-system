package com.ritajshakeel.rrs.persistence;

import java.util.function.Function;

import com.ritajshakeel.rrs.repository.ResourceRepository;

public interface ResourceTransactionManager {
    <T> T doInTransaction(Function<ResourceRepository, T> code);
}