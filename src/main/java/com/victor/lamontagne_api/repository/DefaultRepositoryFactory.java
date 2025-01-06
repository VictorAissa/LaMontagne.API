package com.victor.lamontagne_api.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultRepositoryFactory implements RepositoryFactory {
    private final MongoJourneyRepository mongoJourneyRepository;
    private final MongoUserRepository mongoUserRepository;

    @Autowired
    public DefaultRepositoryFactory(MongoJourneyRepository mongoJourneyRepository, MongoUserRepository mongoUserRepository) {
        this.mongoJourneyRepository = Objects.requireNonNull(mongoJourneyRepository, "JourneyRepository cannot be null");
        this.mongoUserRepository = Objects.requireNonNull(mongoUserRepository, "UserRepository cannot be null");
    }

    @Override
    public JourneyRepository getJourneyRepository() {
        return mongoJourneyRepository;
    }

    @Override
    public UserRepository getUserRepository() {
        return mongoUserRepository;
    }
}