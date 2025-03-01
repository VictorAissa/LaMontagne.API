package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.exception.NotImplementedException;
import com.victor.lamontagne_api.model.dto.JourneyDTO;
import com.victor.lamontagne_api.service.journey.JourneyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/journey")
@CrossOrigin(
    origins = {"http://localhost:5173", "http://localhost:4200"},
    allowCredentials = "true",
    allowedHeaders = {"Authorization", "Content-Type", "Accept"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
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
    public JourneyDTO getJourneyById(@PathVariable String id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return journeyService.getJourneyById(id, userId);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JourneyDTO createJourney(
            @RequestPart("journeyData") JourneyDTO journey,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        return journeyService.createJourney(journey, files, userId);
    }

    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JourneyDTO updateJourney(
            @RequestPart("journeyData") JourneyDTO journey,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        return journeyService.updateJourney(journey.getId(), journey, files, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteJourney(@PathVariable String id, HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        journeyService.deleteJourney(id, userId);
    }

    @DeleteMapping("/{id}/files")
    public void deleteFiles(
            @PathVariable String id,
            @RequestBody List<String> fileUrls
    ) {
        journeyService.deleteFiles(id, fileUrls);
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
