package com.victor.lamontagne_api.service;

import com.victor.lamontagne_api.exception.ResourceNotFoundException;
import com.victor.lamontagne_api.exception.UnauthorizedException;
import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public List<JourneyDTO> getAllJourneys(String userId) {
        return journeyRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public JourneyDTO getJourneyById(String id, String userId) {
        Journey journey = journeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found with id: " + id));

        if (!journey.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this journey");
        }

        return toDto(journey);
    }

    @Override
    public JourneyDTO createJourney(JourneyDTO journeyDto, MultipartFile[] files, String userId) {
        journeyDto.setUserId(userId);

        if (files != null && files.length > 0) {
            List<String> pictureUrls = new ArrayList<>();
            String gpxUrl = null;

            for (MultipartFile file : files) {
                try {
                    String contentType = file.getContentType();
                    if (contentType != null && contentType.startsWith("image/")) {
                        String url = cloudinaryService.uploadImage(file.getBytes());
                        pictureUrls.add(url);
                    } else if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".gpx")) {
                        gpxUrl = cloudinaryService.uploadGpx(file.getBytes());
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error uploading file", e);
                }
            }

            journeyDto.setPictures(pictureUrls);
            journeyDto.getItinerary().setGpx(gpxUrl);
        }

        Journey journey = toPojo(journeyDto);
        journey.setId(null);
        Journey savedJourney = journeyRepository.save(journey);
        return toDto(savedJourney);
    }

    @Override
    public JourneyDTO updateJourney(String id, JourneyDTO journeyDto, MultipartFile[] files, String userId) {
        Journey existingJourney = journeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));

        if (!existingJourney.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this journey");
        }

        if (files != null && files.length > 0) {
            List<String> pictureUrls = new ArrayList<>(journeyDto.getPictures());

            for (MultipartFile file : files) {
                try {
                    String contentType = file.getContentType();
                    if (contentType != null && contentType.startsWith("image/")) {
                        String url = cloudinaryService.uploadImage(file.getBytes());
                        pictureUrls.add(url);
                    } else if (file.getOriginalFilename() != null && file.getOriginalFilename().endsWith(".gpx")) {
                        if (existingJourney.getItinerary().getGpx() != null) {
                            cloudinaryService.deleteFile(cloudinaryService.extractPublicId(existingJourney.getItinerary().getGpx()));
                        }
                        String url = cloudinaryService.uploadGpx(file.getBytes());
                        journeyDto.getItinerary().setGpx(url);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error uploading file", e);
                }
            }
            journeyDto.setPictures(pictureUrls);
        }

        Journey journey = toPojo(journeyDto);
        journey.setId(id);
        Journey updatedJourney = journeyRepository.save(journey);
        return toDto(updatedJourney);
    }

    @Override
    public void deleteJourney(String id, String userId) {
        Journey journey = journeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));

        if (!journey.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this journey");
        }

        if (journey.getPictures() != null) {
            for (String pictureUrl : journey.getPictures()) {
                cloudinaryService.deleteFile(cloudinaryService.extractPublicId(pictureUrl));
            }
        }

        if (journey.getItinerary().getGpx() != null) {
            cloudinaryService.deleteFile(cloudinaryService.extractPublicId(journey.getItinerary().getGpx()));
        }

        journeyRepository.delete(id);
    }

    @Override
    public void deleteFiles(String journeyId, List<String> fileUrls) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));

        for (String fileUrl : fileUrls) {
            cloudinaryService.deleteFile(cloudinaryService.extractPublicId(fileUrl));
        }

        for (String fileUrl : fileUrls) {
            if (fileUrl.endsWith(".gpx")) {
                journey.getItinerary().setGpx(null);
            } else {
                journey.getPictures().remove(fileUrl);
            }
        }

        journeyRepository.save(journey);
    }

    @Override
    public List<JourneyDTO> getPlannedJourneys(String userId) {
        return journeyRepository.findByDateAfterAndUserId(new Date(), userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JourneyDTO> getPastJourneys(String userId) {
        return journeyRepository.findByDateBeforeAndUserId(new Date(), userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private JourneyDTO toDto(Journey journey) {
        return new JourneyDTO(journey);
    }

    private Journey toPojo(JourneyDTO journeyDto) {
        return new Journey(journeyDto);
    }
}
