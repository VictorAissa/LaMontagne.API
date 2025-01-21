package com.victor.lamontagne_api.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultRepositoryFactoryTest {
    @Mock
    private MongoJourneyRepository mongoJourneyRepository;

    @Mock
    private MongoUserRepository mongoUserRepository;

    private DefaultRepositoryFactory defaultRepositoryFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void constructor_ShouldThrowException_WhenJourneyRepositoryIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new DefaultRepositoryFactory(null, mongoUserRepository);
        }, "JourneyRepository cannot be null");
    }

    @Test
    void constructor_ShouldThrowException_WhenUserRepositoryIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new DefaultRepositoryFactory(mongoJourneyRepository, null);
        }, "UserRepository cannot be null");
    }

    @Test
    void getJourneyRepository_ShouldReturnJourneyRepository() {
        // Arrange
        defaultRepositoryFactory = new DefaultRepositoryFactory(mongoJourneyRepository, mongoUserRepository);

        // Act
        JourneyRepository journeyRepository = defaultRepositoryFactory.getJourneyRepository();

        // Assert
        assertNotNull(journeyRepository);
    }

    @Test
    void getUserRepository_ShouldReturnUserRepository() {
        // Arrange
        defaultRepositoryFactory = new DefaultRepositoryFactory(mongoJourneyRepository, mongoUserRepository);

        // Act
        UserRepository userRepository = defaultRepositoryFactory.getUserRepository();

        // Assert
        assertNotNull(userRepository);
    }
}