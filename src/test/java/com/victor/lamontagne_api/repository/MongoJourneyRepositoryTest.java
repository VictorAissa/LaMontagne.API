package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.Journey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MongoJourneyRepositoryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private MongoJourneyRepository mongoJourneyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mongoJourneyRepository = new MongoJourneyRepository(mongoTemplate);
    }

    @Test
    void findAll_ShouldReturnAllJourneys() {
        // Given
        List<Journey> journeys = List.of(new Journey(), new Journey());
        when(mongoTemplate.findAll(Journey.class)).thenReturn(journeys);

        // When
        List<Journey> result = mongoJourneyRepository.findAll();

        // Then
        assertEquals(journeys, result);
    }

    @Test
    void findById_ShouldReturnJourney_WhenJourneyExists() {
        // Given
        String id = "123";
        Journey journey = new Journey();
        when(mongoTemplate.findById(id, Journey.class)).thenReturn(journey);

        // When
        Optional<Journey> result = mongoJourneyRepository.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(journey, result.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenJourneyDoesNotExist() {
        // Given
        String id = "123";
        when(mongoTemplate.findById(id, Journey.class)).thenReturn(null);

        // When
        Optional<Journey> result = mongoJourneyRepository.findById(id);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAllByUserId_ShouldReturnJourneys_WhenUserIdExists() {
        // Given
        String userId = "user123";
        List<Journey> journeys = List.of(new Journey(), new Journey());
        Query query = new Query(Criteria.where("userId").is(userId));
        when(mongoTemplate.find(query, Journey.class)).thenReturn(journeys);

        // When
        List<Journey> result = mongoJourneyRepository.findAllByUserId(userId);

        // Then
        assertEquals(journeys, result);
    }

    @Test
    void findByDateAfterAndUserId_ShouldReturnJourneys() {
        // Given
        String userId = "user123";
        Date date = new Date();
        List<Journey> journeys = List.of(new Journey(), new Journey());
        Query query = new Query(Criteria.where("date").gt(date).and("userId").is(userId));
        when(mongoTemplate.find(query, Journey.class)).thenReturn(journeys);

        // When
        List<Journey> result = mongoJourneyRepository.findByDateAfterAndUserId(date, userId);

        // Then
        assertEquals(journeys, result);
    }

    @Test
    void findByDateBeforeAndUserId_ShouldReturnJourneys() {
        // Given
        String userId = "user123";
        Date date = new Date();
        List<Journey> journeys = List.of(new Journey(), new Journey());
        Query query = new Query(Criteria.where("date").lt(date).and("userId").is(userId));
        when(mongoTemplate.find(query, Journey.class)).thenReturn(journeys);

        // When
        List<Journey> result = mongoJourneyRepository.findByDateBeforeAndUserId(date, userId);

        // Then
        assertEquals(journeys, result);
    }

    @Test
    void save_ShouldSaveJourney() {
        // Given
        Journey journey = new Journey();
        when(mongoTemplate.save(journey)).thenReturn(journey);

        // When
        Journey result = mongoJourneyRepository.save(journey);

        // Then
        assertEquals(journey, result);
    }

    @Test
    void delete_ShouldRemoveJourneyById() {
        // Given
        String id = "123";
        Query query = new Query(Criteria.where("id").is(id));

        // When
        mongoJourneyRepository.delete(id);

        // Then
        verify(mongoTemplate).remove(query, Journey.class);
    }
}