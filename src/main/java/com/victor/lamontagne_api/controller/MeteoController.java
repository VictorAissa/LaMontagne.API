package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.model.pojo.Meteo;
import com.victor.lamontagne_api.service.meteo.MeteoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/meteo")
@CrossOrigin(
        origins = {"http://localhost:5173", "http://localhost:4200"},
        allowCredentials = "true",
        allowedHeaders = {"Authorization", "Content-Type", "Accept"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class MeteoController {
    private final MeteoService meteoService;

    @Autowired
    public MeteoController(MeteoService meteoService) {
        this.meteoService = meteoService;
    }

    @GetMapping("/journey/{journeyId}")
    public Meteo getMeteoForJourney(@PathVariable String journeyId) {
        return meteoService.getMeteoData(journeyId);
    }

    @GetMapping
    public Meteo getMeteoData(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        return meteoService.getMeteoData(latitude, longitude, date);
    }

    @PutMapping("/journey/{journeyId}/refresh")
    public Meteo refreshMeteoData(@PathVariable String journeyId) {
        return meteoService.refreshMeteoData(journeyId);
    }
}
