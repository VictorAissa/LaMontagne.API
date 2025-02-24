package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;

import java.util.Date;

public interface MeteoService {
    Meteo getMeteoData(String journeyId);

    Meteo getMeteoData(double latitude, double longitude, Date date);

    Meteo refreshMeteoData(String journeyId);
}