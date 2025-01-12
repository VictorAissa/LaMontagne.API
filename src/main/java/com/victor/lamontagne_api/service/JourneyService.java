package com.victor.lamontagne_api.service;

import com.victor.lamontagne_api.model.dto.JourneyDTO;

import java.util.List;

public interface JourneyService {
    List<JourneyDTO> getAllJourneys();
    JourneyDTO getJourneyById(String id);
    JourneyDTO createJourney(JourneyDTO journey);
    JourneyDTO updateJourney(String id, JourneyDTO journey);
    void deleteJourney(String id);
}