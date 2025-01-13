package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.exception.NotImplementedException;
import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.service.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @GetMapping("/user/{userId}")
    public List<JourneyDTO> getAllJourneys(@PathVariable String userId) {
        return journeyService.getAllJourneys(userId);
    }

    @GetMapping("/{id}")
    public JourneyDTO getJourneyById(@PathVariable String id) {
        return journeyService.getJourneyById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JourneyDTO createJourney(
            @RequestPart("journeyData") JourneyDTO journey,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        return journeyService.createJourney(journey, files);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JourneyDTO updateJourney(
            @PathVariable String id,
            @RequestPart("journeyData") JourneyDTO journey,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        return journeyService.updateJourney(id, journey, files);
    }

    @DeleteMapping("/{id}")
    public void deleteJourney(@PathVariable String id) {
        journeyService.deleteJourney(id);
    }

    @PostMapping("/{id}/files")
    public void uploadFiles(
            @PathVariable String id,
            @RequestParam("files") MultipartFile[] files
    ) {
        //journeyService.uploadFiles(id, files);
        throw new NotImplementedException("upload files not implemented");
    }
}
