package com.victor.lamontagne_api.repository;

public interface RepositoryFactory {
    JourneyRepository getJourneyRepository();
    UserRepository getUserRepository();
}
