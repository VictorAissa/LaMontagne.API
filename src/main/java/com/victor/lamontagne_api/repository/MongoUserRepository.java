package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MongoUserRepository implements UserRepository {
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }
}
