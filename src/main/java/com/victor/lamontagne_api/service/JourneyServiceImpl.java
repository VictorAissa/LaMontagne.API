package com.victor.lamontagne_api.service;

import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class JourneyServiceImpl implements JourneyService {
    private final JourneyRepository journeyRepository;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public JourneyServiceImpl(JourneyRepository journeyRepository, CloudinaryService cloudinaryService) {
        this.journeyRepository = Objects.requireNonNull(journeyRepository, "JourneyRepository cannot be null");
        this.cloudinaryService = Objects.requireNonNull(cloudinaryService, "CloudinaryService cannot be null");
    }

    @Override
    public List<JourneyDTO> getAllJourneys() {
        return List.of();
    }

    @Override
    public JourneyDTO getJourneyById(String id) {
        return null;
    }

    @Override
    public JourneyDTO createJourney(JourneyDTO journey) {
        return null;
    }

    @Override
    public JourneyDTO updateJourney(String id, JourneyDTO journey) {
        return null;
    }

    @Override
    public void deleteJourney(String id) {

    }
}
