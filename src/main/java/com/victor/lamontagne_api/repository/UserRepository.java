package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
}
