package com.ritajshakeel.rrs.persistence;

public interface TransactionManager {
    <T> T doInTransaction(TransactionCode<T> code);
}