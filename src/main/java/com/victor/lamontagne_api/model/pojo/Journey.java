package com.victor.lamontagne_api.model.pojo;

import com.victor.lamontagne_api.model.*;
import com.victor.lamontagne_api.model.dto.JourneyDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "journeys")
@NoArgsConstructor
public class Journey{
    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    @Indexed
    private Date date;

    @NotBlank
    @Indexed
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

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;

    public Journey(JourneyDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.date = dto.getDate();
        this.userId = dto.getUserId();
        this.season = dto.getSeason();
        this.members = dto.getMembers();
        this.pictures = dto.getPictures();
        this.itinerary = dto.getItinerary();
        this.altitudes = dto.getAltitudes();
        this.meteo = dto.getMeteo();
        this.protections = dto.getProtections();
        this.miscellaneous = dto.getMiscellaneous();
    }
}