package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.config.MeteoBlueConfig;
import com.victor.lamontagne_api.model.enums.Direction;
import com.victor.lamontagne_api.model.enums.Sky;
import com.victor.lamontagne_api.model.external.response.meteoblue.DataDay;
import com.victor.lamontagne_api.model.external.response.meteoblue.MeteoBlueResponse;
import com.victor.lamontagne_api.model.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@ConditionalOnProperty(name = "app.meteo.providers.meteoblue.enabled", havingValue = "true")
public class MeteoBlueProvider implements MeteoProvider {
    private final WebClient webClient;
    private final MeteoBlueConfig meteoblueConfig;
    private static final Logger logger = LoggerFactory.getLogger(MeteoBlueProvider.class);

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
        try {
            String url = meteoblueConfig.buildRequestUrl(latitude, longitude);
            logger.debug("Calling MeteoBluе API with URL: {}", url);

            MeteoBlueResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(MeteoBlueResponse.class)
                    .block();

            if (response == null) {
                logger.error("Null response from MeteoBluе API");
                return createDefaultMeteo();
            }

            if (response.getDataDay() == null) {
                logger.error("Null dataDay in response");
                return createDefaultMeteo();
            }

            int dayIndex = findDayIndex(response, journeyDate);
            return mapToMeteo(response, dayIndex);
        } catch (Exception e) {
            logger.error("Error fetching meteo data: {}", e.getMessage(), e);
            return createDefaultMeteo();
        }
    }

    private Meteo createDefaultMeteo() {
        return new Meteo(
                Sky.PARTLY_CLOUDY,
                new Temperature(0, 0),
                new Iso(0, 0),
                new Wind(Direction.N, 0),
                0
        );
    }

    @Override
    public Integer getBera(String massifId) {
        throw new UnsupportedOperationException("No BERA available in MeteoBluе");
    }

    private Meteo mapToMeteo(MeteoBlueResponse response, int dayIndex) {
        DataDay dayData = response.getDataDay();

        if (dayData.getPictocode() == null || dayData.getPictocode().size() <= dayIndex) {
            logger.error("Missing pictocode data for day index: {}", dayIndex);
            return createDefaultMeteo();
        }

        Sky sky = mapPictocodeToSky(dayData.getPictocode().get(dayIndex));

        Temperature temperature = new Temperature(
                (int) Math.round(dayData.getTemperatureMax().get(dayIndex)),
                (int) Math.round(dayData.getTemperatureMin().get(dayIndex))
        );

        Iso iso = new Iso(
                getIsoZeroNight(response, dayIndex),
                getIsoZeroDay(response, dayIndex)
        );

        Wind wind = new Wind(
                mapWindDirection(dayData.getWinddirection().get(dayIndex)),
                (int)(dayData.getWindspeedMean().get(dayIndex) * 3.6)
        );

        return new Meteo(sky, temperature, iso, wind, 0);
    }

    private int getIsoZeroDay(MeteoBlueResponse response, int dayIndex) {
        try {
            int index6h = response.getDataDay().getIndexto6hvaluesStart().get(dayIndex) + 2;

            if (index6h >= response.getData6h().getFreezinglevelheightMean().size()) {
                logger.warn("Index 6h out of bounds for iso zero day: {}", index6h);
                return 0;
            }

            return (int) Math.round(response.getData6h().getFreezinglevelheightMean().get(index6h));
        } catch (Exception e) {
            logger.error("Error getting iso zero day: {}", e.getMessage());
            return 0;
        }
    }

    private int getIsoZeroNight(MeteoBlueResponse response, int dayIndex) {
        try {
            int index6h = response.getDataDay().getIndexto6hvaluesStart().get(dayIndex);

            if (index6h >= response.getData6h().getFreezinglevelheightMean().size()) {
                logger.warn("Index 6h out of bounds for iso zero night: {}", index6h);
                return 0;
            }

            return (int) Math.round(response.getData6h().getFreezinglevelheightMean().get(index6h));
        } catch (Exception e) {
            logger.error("Error getting iso zero night: {}", e.getMessage());
            return 0;
        }
    }

    private Sky mapPictocodeToSky(int pictocode) {
        switch(pictocode) {
            case 1: case 2:
                return Sky.SUNNY;
            case 3: case 7: case 10:
                return Sky.PARTLY_CLOUDY;
            case 4: case 5:
                return Sky.CLOUDY;
            case 7: case 12: case 16:
                return Sky.LIGHT_RAIN;
            case 6: case 8: case 14:
                return Sky.HEAVY_RAIN;
            case 10: case 11: case 13: case 17:
                return Sky.LIGHT_SNOW;
            case 9: case 15:
                return Sky.HEAVY_SNOW;
            default:
                return Sky.PARTLY_CLOUDY;
        }
    }

    private Direction mapWindDirection(int direction) {
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
        LocalDate targetDate = journeyDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        List<String> timeList = response.getDataDay().getTime();
        for (int i = 0; i < timeList.size(); i++) {
            LocalDate responseDate = LocalDate.parse(timeList.get(i));
            if (responseDate.equals(targetDate)) {
                return i;
            }
        }

        return Math.min(6, timeList.size() - 1);
    }
}