package com.ritajshakeel.rrs.persistence;

import java.util.function.Function;

import com.ritajshakeel.rrs.repository.ReservationRepository;

public interface ReservationTransactionManager {
	<T> T doInTransaction(Function<ReservationRepository, T> code);
}