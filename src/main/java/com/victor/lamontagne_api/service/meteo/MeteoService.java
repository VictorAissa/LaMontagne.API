package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;

public interface MeteoService {
    Meteo getMeteoData(Journey journey);
}