package com.victor.lamontagne_api.controller;

import com.victor.lamontagne_api.model.pojo.Meteo;
import com.victor.lamontagne_api.model.request.MeteoRequest;
import com.victor.lamontagne_api.service.meteo.MeteoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/meteo")
@CrossOrigin(
        origins = {"http://localhost:5173", "https://lamontagneapp.vercel.app/"},
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

    @PostMapping
    public Meteo getMeteoData(@RequestBody @Valid MeteoRequest request) {
        Date parsedDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            parsedDate = dateFormat.parse(request.date());
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad date format, use yyyy-MM-dd");
        }

        return meteoService.getMeteoData(request.latitude(), request.longitude(), parsedDate);
    }

    @PutMapping("/journey/{journeyId}/refresh")
    public Meteo refreshMeteoData(@PathVariable String journeyId) {
        return meteoService.refreshMeteoData(journeyId);
    }
}
