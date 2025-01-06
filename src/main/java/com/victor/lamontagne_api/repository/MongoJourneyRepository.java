package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.Journey;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class MongoJourneyRepository implements JourneyRepository {

    @Override
    public List<Journey> findAll() {
        return List.of();
    }

    @Override
    public Optional<Journey> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Journey> findByDateAfter(Date date) {
        return List.of();
    }

    @Override
    public List<Journey> findByDateBefore(Date date) {
        return List.of();
    }

    @Override
    public Journey save(Journey journey) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
