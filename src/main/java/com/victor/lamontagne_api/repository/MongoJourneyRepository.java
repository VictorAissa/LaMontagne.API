package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.Journey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class MongoJourneyRepository implements JourneyRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoJourneyRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Journey> findAll() {
        return mongoTemplate.findAll(Journey.class);
    }

    @Override
    public Optional<Journey> findById(String id) {
        Journey journey = mongoTemplate.findById(id, Journey.class);
        return Optional.ofNullable(journey);
    }

    @Override
    public List<Journey> findAllByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Journey.class);
    }

    @Override
    public List<Journey> findByDateAfterAndUserId(Date date, String userId) {
        Query query = new Query(Criteria.where("date").gt(date)
                .and("userId").is(userId));
        return mongoTemplate.find(query, Journey.class);
    }

    @Override
    public List<Journey> findByDateBeforeAndUserId(Date date, String userId) {
        Query query = new Query(Criteria.where("date").lt(date)
                .and("userId").is(userId));
        return mongoTemplate.find(query, Journey.class);
    }

    @Override
    public Journey save(Journey journey) {
        return mongoTemplate.save(journey);
    }

    @Override
    public void delete(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, Journey.class);
    }
}