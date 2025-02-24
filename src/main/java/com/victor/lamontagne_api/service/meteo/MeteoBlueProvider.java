package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.config.MeteoBlueConfig;
import com.victor.lamontagne_api.exception.NotImplementedException;
import com.victor.lamontagne_api.model.pojo.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@ConditionalOnProperty(name = "app.meteo.providers.meteoblue.enabled", havingValue = "true")
public class MeteoBlueProvider implements MeteoProvider {
    private final WebClient webClient;
    private final MeteoBlueConfig meteoblueConfig;

    @Autowired
    public MeteoBlueProvider(WebClient meteoblueWebClient, MeteoBlueConfig meteoblueConfig) {
        this.webClient = meteoblueWebClient;
        this.meteoblueConfig = meteoblueConfig;
    }

    @Override
    public void accept(MeteoVisitor visitor) {
        visitor.visitMeteoblue(this);
    }

    @Override
    public Meteo getMeteoData(double latitude, double longitude, Date journeyDate) {
        String url = meteoblueConfig.buildRequestUrl(latitude, longitude);

        MeteoBlueResponse response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(MeteoBlueResponse.class)
                .block();

        int dayIndex = findDayIndex(response, journeyDate);

        return mapToMeteo(response, dayIndex);
    }

    @Override
    public Integer getBera(String massifName) {
        throw new NotImplementedException("No BERA available in MeteoBlue");
    }

    private Meteo mapToMeteo(MeteoBlueResponse response, int dayIndex) {
        DataDay dayData = response.getDataDay();
        Sky sky = mapPictocodeToSky(dayData.getPictocode()[dayIndex]);

        Temperature temperature = new Temperature(
                (int) Math.round(dayData.getTemperatureMax()[dayIndex]),
                (int) Math.round(dayData.getTemperatureMin()[dayIndex])
        );

        Iso iso = new Iso(
                getIsoZeroNight(response, dayIndex),
                getIsoZeroDay(response, dayIndex)
        );

        Wind wind = new Wind(
                mapWindDirection(dayData.getWinddirection()[dayIndex]),
                // Conversion m/s en km/h
                (int)(dayData.getWindspeedMean()[dayIndex] * 3.6)
        );

        return new Meteo(sky, temperature, iso, wind, 0); // BERA sera ajouté par MeteoFrance
    }

    private int getIsoZeroDay(MeteoBlueResponse response, int dayIndex) {
        // Convertir l'index jour en index pour les données 6h (milieu de journée)
        int index6h = response.getDataDay().getIndexto6hvaluesStart()[dayIndex] + 2;
        return (int) response.getData6h().getFreezinglevelheigtMean()[index6h];
    }

    private int getIsoZeroNight(MeteoBlueResponse response, int dayIndex) {
        // Convertir l'index jour en index pour les données 6h (milieu de nuit)
        int index6h = response.getDataDay().getIndexto6hvaluesStart()[dayIndex];
        return (int) response.getData6h().getFreezinglevelheigtMean()[index6h];
    }

    private Sky mapPictocodeToSky(int pictocode) {
        switch(pictocode) {
            case 1: case 2: case 3:
                return Sky.SUNNY;
            case 4: case 5:
                return Sky.PARTLY_CLOUDY;
            case 6: case 7:
                return Sky.PARTLY_CLOUDY;
            case 8: case 9: case 10:
                return Sky.CLOUDY;
            case 11: case 12: case 13: case 14:
                return Sky.RAIN;
            case 15: case 16:
                return Sky.LIGHT_SNOW;
            case 17: case 18: case 19: case 20:
                return Sky.HEAVY_SNOW;
            default:
                return Sky.PARTLY_CLOUDY;
        }
    }

    private Direction mapWindDirection(int direction) {
        // Simplification de la direction du vent en 8 directions
        if (direction >= 337.5 || direction < 22.5) return Direction.N;
        if (direction >= 22.5 && direction < 67.5) return Direction.NE;
        if (direction >= 67.5 && direction < 112.5) return Direction.E;
        if (direction >= 112.5 && direction < 157.5) return Direction.SE;
        if (direction >= 157.5 && direction < 202.5) return Direction.S;
        if (direction >= 202.5 && direction < 247.5) return Direction.SW;
        if (direction >= 247.5 && direction < 292.5) return Direction.W;
        return Direction.NW;
    }

    private int findDayIndex(MeteoBlueResponse response, Date journeyDate) {
        // Trouver l'index du jour correspondant à la date de la course
        LocalDate targetDate = journeyDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        for (int i = 0; i < response.getDataDay().getTime().length; i++) {
            LocalDate responseDate = LocalDate.parse(response.getDataDay().getTime()[i]);
            if (responseDate.equals(targetDate)) {
                return i;
            }
        }

        // Si la date est au-delà des 7 jours, prendre le dernier jour disponible
        return Math.min(6, response.getDataDay().getTime().length - 1);
    }
}

@Getter
@Setter
class MeteoBlueResponse {
    private DataDay dataDay;
    private Data6h data6h;
}

@Getter
@Setter
class DataDay {
    private String[] time;
    private int[] pictocode;
    private double[] temperatureMax;
    private double[] temperatureMin;
    private int[] winddirection;
    private double[] windspeedMean;
    private int[] indexto6hvaluesStart;
}

@Getter
@Setter
class Data6h {
    private String[] time;
    private double[] freezinglevelheigtMean;
}

