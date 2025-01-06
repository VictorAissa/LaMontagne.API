package com.victor.lamontagne_api.model.dto;

import com.victor.lamontagne_api.model.*;
import com.victor.lamontagne_api.model.pojo.Journey;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class JourneyDTO {
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private Date date;

    @NotBlank
    private String userId;

    @NotBlank
    private Season season;

    private List<String> members;
    private List<String> pictures;
    private Itinerary itinerary;
    private Altitudes altitudes;
    private Meteo meteo;
    private Protections protections;
    private String miscellaneous;

    public JourneyDTO(Journey journey) {
        this.id = journey.getId();
        this.title = journey.getTitle();
        this.date = journey.getDate();
        this.userId = journey.getUserId();
        this.season = journey.getSeason();
        this.members = journey.getMembers();
        this.pictures = journey.getPictures();
        this.itinerary = journey.getItinerary();
        this.altitudes = journey.getAltitudes();
        this.meteo = journey.getMeteo();
        this.protections = journey.getProtections();
        this.miscellaneous = journey.getMiscellaneous();
    }
}