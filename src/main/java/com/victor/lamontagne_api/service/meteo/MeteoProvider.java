package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.model.pojo.Meteo;

import java.util.Date;

public interface MeteoProvider {
    void accept(MeteoVisitor visitor);

    Meteo getMeteoData(double latitude, double longitude, Date date);

    Integer getBera(String massifName);
}
