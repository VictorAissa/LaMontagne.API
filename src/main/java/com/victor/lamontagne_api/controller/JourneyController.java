package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/journey")
public class JourneyController {
    private final JourneyService journeyService;

    @Autowired
    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping
    public String getAllJourneys() { // List<JourneyDTO>
        String test = "toutes les courses !";
        return test;
        //return journeyService.getAllJourneys();
    }

    @GetMapping("/{id}")
    public JourneyDTO getJourneyById(String id) {
        return journeyService.getJourneyById(id);
    }

    @PostMapping
    public JourneyDTO createJourney(JourneyDTO journey) {
        return journeyService.createJourney(journey);
    }

    @PutMapping("/{id}")
    public JourneyDTO updateJourney(String id, JourneyDTO journey) {
        return journeyService.updateJourney(id, journey);
    }

    @DeleteMapping("/{id}")
    public void deleteJourney(String id) {
        journeyService.deleteJourney(id);
    }

    @PostMapping("/{id}/files")
    public void uploadFiles(String id, MultipartFile[] files) {
        //TODO
    }
}
