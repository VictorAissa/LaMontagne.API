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
        if (isInFrance(new GeoPoint(latitude, longitude))) {
            meteo.setBera(provider.getBera(getMassifName(new GeoPoint(latitude, longitude))));
        }
    }

    private boolean isInFrance(GeoPoint point) {
        return point.getLatitude() >= 41.0 && point.getLatitude() <= 51.5 &&
                point.getLongitude() >= -5.0 && point.getLongitude() <= 10.0;
    }

    private String getMassifName(GeoPoint point) {
        double lat = point.getLatitude();
        double lon = point.getLongitude();

        // Alpes du Nord
        if (lat >= 45.0 && lat <= 46.5 && lon >= 5.5 && lon <= 7.5) {
            return "ALPES_DU_NORD";
        }
        // Alpes du Sud
        else if (lat >= 43.5 && lat <= 45.0 && lon >= 5.5 && lon <= 7.5) {
            return "ALPES_DU_SUD";
        }
        // Pyrénées
        else if (lat >= 42.5 && lat <= 43.5 && lon >= -1.0 && lon <= 3.0) {
            return "PYRENEES";
        }
        // Jura
        else if (lat >= 46.0 && lat <= 47.5 && lon >= 5.0 && lon <= 7.0) {
            return "JURA";
        }
        // Vosges
        else if (lat >= 47.5 && lat <= 49.0 && lon >= 6.0 && lon <= 8.0) {
            return "VOSGES";
        }
        // Corse
        else if (lat >= 41.5 && lat <= 43.0 && lon >= 8.5 && lon <= 9.5) {
            return "CORSE";
        }
        // Massif Central
        else if (lat >= 44.0 && lat <= 46.0 && lon >= 2.0 && lon <= 4.5) {
            return "MASSIF_CENTRAL";
        }
        else {
            return "INCONNU";
        }
    }
}