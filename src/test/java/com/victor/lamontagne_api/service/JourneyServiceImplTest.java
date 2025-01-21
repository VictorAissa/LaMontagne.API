package com.victor.lamontagne_api.service;

import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.model.pojo.*;
import com.victor.lamontagne_api.repository.JourneyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JourneyServiceImplTest {
    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    private JourneyServiceImpl journeyService;
    private static final String USER_ID = "user123";
    private static final String JOURNEY_ID = "journey123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        journeyService = new JourneyServiceImpl(journeyRepository, cloudinaryService);
    }


    @Test
    void getAllJourneys_ShouldReturnAllUserJourneys() {
        // Given
        String userId = "user123";
        List<Journey> journeys = Arrays.asList(
                createJourney("Journey 1"),
                createJourney("Journey 2")
        );
        when(journeyRepository.findAllByUserId(userId)).thenReturn(journeys);

        // When
        List<JourneyDTO> result = journeyService.getAllJourneys(userId);

        // Then
        assertEquals(2, result.size());
        assertEquals("Journey 1", result.get(0).getTitle());
        verify(journeyRepository).findAllByUserId(userId);
    }

    @Test
    void getJourneyById_ShouldReturnJourney() {
        // Given
        Journey journey = createJourney("Test Journey");
        when(journeyRepository.findById(JOURNEY_ID)).thenReturn(Optional.of(journey));

        // When
        JourneyDTO result = journeyService.getJourneyById(JOURNEY_ID, USER_ID);

        // Then
        assertEquals("Test Journey", result.getTitle());
        verify(journeyRepository).findById(JOURNEY_ID);
    }

    @Test
    void createJourney_ShouldCreateAndReturnJourney() throws Exception {
        // Given
        JourneyDTO inputJourney = createJourneyDTO("New Journey");
        Journey savedJourney = createJourney("New Journey");
        savedJourney.setId(JOURNEY_ID);

        MockMultipartFile imageFile = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "test".getBytes()
        );
        when(cloudinaryService.uploadImage(any())).thenReturn("image_url");
        when(journeyRepository.save(any(Journey.class))).thenReturn(savedJourney);

        // When
        JourneyDTO result = journeyService.createJourney(inputJourney, new MockMultipartFile[]{imageFile}, USER_ID);

        // Then
        assertEquals(JOURNEY_ID, result.getId());
        assertEquals("New Journey", result.getTitle());
        verify(journeyRepository).save(any(Journey.class));
    }

    @Test
    void updateJourney_ShouldUpdateAndReturnJourney() throws Exception {
        // Given
        Journey existingJourney = createJourney("Old Journey");
        JourneyDTO updateData = createJourneyDTO("Updated Journey");
        Journey updatedJourney = createJourney("Updated Journey");
        updatedJourney.setId(JOURNEY_ID);

        when(journeyRepository.findById(JOURNEY_ID)).thenReturn(Optional.of(existingJourney));
        when(journeyRepository.save(any(Journey.class))).thenReturn(updatedJourney);

        // When
        JourneyDTO result = journeyService.updateJourney(JOURNEY_ID, updateData, null, USER_ID);

        // Then
        assertEquals("Updated Journey", result.getTitle());
        verify(journeyRepository).save(any(Journey.class));
    }

    @Test
    void getPlannedJourneys_ShouldReturnFutureJourneys() {
        // Given
        List<Journey> plannedJourneys = Arrays.asList(
                createJourney("Future Journey 1"),
                createJourney("Future Journey 2")
        );
        when(journeyRepository.findByDateAfterAndUserId(any(Date.class), eq(USER_ID)))
                .thenReturn(plannedJourneys);

        // When
        List<JourneyDTO> result = journeyService.getPlannedJourneys(USER_ID);

        // Then
        assertEquals(2, result.size());
        verify(journeyRepository).findByDateAfterAndUserId(any(Date.class), eq(USER_ID));
    }

    @Test
    void getPastJourneys_ShouldReturnPastJourneys() {
        // Given
        List<Journey> pastJourneys = Arrays.asList(
                createJourney("Past Journey 1"),
                createJourney("Past Journey 2")
        );
        when(journeyRepository.findByDateBeforeAndUserId(any(Date.class), eq(USER_ID)))
                .thenReturn(pastJourneys);

        // When
        List<JourneyDTO> result = journeyService.getPastJourneys(USER_ID);

        // Then
        assertEquals(2, result.size());
        verify(journeyRepository).findByDateBeforeAndUserId(any(Date.class), eq(USER_ID));
    }

    // Helper methods
    private Journey createJourney(String title) {
        Journey journey = new Journey();
        journey.setId(JOURNEY_ID);
        journey.setTitle(title);
        journey.setUserId(USER_ID);
        journey.setDate(new Date());
        journey.setSeason(Season.SUMMER);
        journey.setMembers(new ArrayList<>());
        journey.setPictures(new ArrayList<>());

        // Initialiser les objets imbriqués
        Itinerary itinerary = new Itinerary();
        itinerary.setStart(new GeoPoint());
        itinerary.setEnd(new GeoPoint());
        journey.setItinerary(itinerary);

        journey.setAltitudes(new Altitudes());
        journey.setMeteo(new Meteo());
        journey.setProtections(new Protections());

        return journey;
    }

    private JourneyDTO createJourneyDTO(String title) {
        JourneyDTO journey = new JourneyDTO();
        journey.setTitle(title);
        journey.setUserId(USER_ID);
        journey.setDate(new Date());
        journey.setSeason(Season.SUMMER);
        journey.setMembers(new ArrayList<>());
        journey.setPictures(new ArrayList<>());

        // Initialiser les objets imbriqués
        Itinerary itinerary = new Itinerary();
        itinerary.setStart(new GeoPoint());
        itinerary.setEnd(new GeoPoint());
        journey.setItinerary(itinerary);

        journey.setAltitudes(new Altitudes());
        journey.setMeteo(new Meteo());
        journey.setProtections(new Protections());

        return journey;
    }
}