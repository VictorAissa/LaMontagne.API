package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.model.pojo.GeoPoint;
import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;
import lombok.Getter;

import java.util.Date;

public class MeteoDataCollector implements MeteoVisitor {
    private final double latitude;
    private final double longitude;
    private final Date date;

    @Getter
    private Meteo meteo;

    public MeteoDataCollector(Journey journey) {
        this.latitude = journey.getItinerary().getStart().getLatitude();
        this.longitude = journey.getItinerary().getStart().getLongitude();
        this.date = journey.getDate();
    }

    public MeteoDataCollector(double latitude, double longitude, Date date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    @Override
    public void visitMeteoblue(MeteoBlueProvider provider) {
        meteo = provider.getMeteoData(latitude, longitude, date);
    }

    @Override
    public void visitMeteoFrance(MeteoFranceProvider provider) {
        if (meteo == null) {
            meteo = new Meteo();
        }

        Integer beraIndex = provider.getBera(new GeoPoint(latitude, longitude), date);

        if (beraIndex != null) {
            meteo.setBera(beraIndex);
        }
    }
}