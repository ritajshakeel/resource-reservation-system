package com.ritajshakeel.rrs.persistence;

import java.util.function.Function;

import com.ritajshakeel.rrs.repository.ReservationRepository;

@FunctionalInterface
public interface TransactionCode<T> extends Function<ReservationRepository, T> {
}