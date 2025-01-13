package com.victor.lamontagne_api.service;

import com.victor.lamontagne_api.model.dto.JourneyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JourneyService {
    List<JourneyDTO> getAllJourneys(String userId);
    JourneyDTO getJourneyById(String id);
    JourneyDTO createJourney(JourneyDTO journey, MultipartFile[] files);
    JourneyDTO updateJourney(String id, JourneyDTO journey, MultipartFile[] files);
    void deleteJourney(String id);
    List<JourneyDTO> getPlannedJourneys(String userId);
    List<JourneyDTO> getPastJourneys(String userId);
}