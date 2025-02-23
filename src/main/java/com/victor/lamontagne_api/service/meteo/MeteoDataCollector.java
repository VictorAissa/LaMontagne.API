package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.model.pojo.GeoPoint;
import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;
import lombok.Getter;
import org.springframework.stereotype.Service;

public class MeteoDataCollector implements MeteoVisitor {
    private final Journey journey;
    @Getter
    private Meteo meteo;

    public MeteoDataCollector(Journey journey) {
        this.journey = journey;
    }

    @Override
    public void visitMeteoblue(MeteoBlueProvider provider) {
        meteo = provider.getMeteoData(
                journey.getItinerary().getStart().getLatitude(),
                journey.getItinerary().getStart().getLongitude(),
                journey.getAltitudes().getMax(),
                journey.getDate()
        );
    }

    @Override
    public void visitMeteoFrance(MeteoFranceProvider provider) {
        if (isInFrance(journey.getItinerary().getStart())) {
            meteo.setBera(provider.getBera(getMassifName(journey)));
        }
    }

    private boolean isInFrance(GeoPoint startingPoint) {
        return true;
    }

    private String getMassifName(Journey journey) {
        return "Mont Blanc";
    }
}