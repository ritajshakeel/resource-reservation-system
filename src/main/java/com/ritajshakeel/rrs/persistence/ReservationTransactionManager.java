package com.ritajshakeel.rrs.persistence;

public interface ReservationTransactionManager {
    <T> T doInTransaction(TransactionCode<T> code);
}