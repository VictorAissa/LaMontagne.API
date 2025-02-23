package com.victor.lamontagne_api.service.journey;

import com.victor.lamontagne_api.model.dto.JourneyDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JourneyService {
    List<JourneyDTO> getAllJourneys(String userId);
    JourneyDTO getJourneyById(String id, String userId);
    JourneyDTO createJourney(JourneyDTO journey, MultipartFile[] files, String userId);
    JourneyDTO updateJourney(String id, JourneyDTO journey, MultipartFile[] files, String userId);
    void deleteJourney(String id, String userId);
    void deleteFiles(String journeyId, List<String> fileUrls);
    List<JourneyDTO> getPlannedJourneys(String userId);
    List<JourneyDTO> getPastJourneys(String userId);
}