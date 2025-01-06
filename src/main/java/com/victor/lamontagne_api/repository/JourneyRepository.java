package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.Journey;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JourneyRepository{
    List<Journey> findAll();
    Optional<Journey> findById(String id);
    List<Journey> findByDateAfter(Date date);
    List<Journey> findByDateBefore(Date date);
    Journey save(Journey journey);
    void delete(String id);
}
