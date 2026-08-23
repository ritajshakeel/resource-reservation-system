package com.ritajshakeel.rrs.repository;

import java.util.List;

import com.ritajshakeel.rrs.domain.User;

public interface UserRepository {
    User save(User user);
    List<User> findAll();
}