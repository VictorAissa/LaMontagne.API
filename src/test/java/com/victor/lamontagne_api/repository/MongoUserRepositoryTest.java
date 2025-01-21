package com.victor.lamontagne_api.repository;

import com.victor.lamontagne_api.model.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class MongoUserRepositoryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private MongoUserRepository mongoUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mongoUserRepository = new MongoUserRepository(mongoTemplate);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email); // Assurez-vous que l'utilisateur a le bon email
        Query query = new Query(Criteria.where("email").is(email));
        when(mongoTemplate.findOne(query, User.class)).thenReturn(user);

        // When
        Optional<User> result = mongoUserRepository.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        String email = "test@example.com";
        Query query = new Query(Criteria.where("email").is(email));
        when(mongoTemplate.findOne(query, User.class)).thenReturn(null);

        // When
        Optional<User> result = mongoUserRepository.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldSaveUser() {
        // Given
        User user = new User();
        when(mongoTemplate.save(user)).thenReturn(user);

        // When
        User result = mongoUserRepository.save(user);

        // Then
        assertEquals(user, result);
    }
}