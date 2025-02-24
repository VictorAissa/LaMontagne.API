package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.exception.NotImplementedException;
import com.victor.lamontagne_api.model.pojo.Meteo;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MeteoFranceProvider implements MeteoProvider {
    @Override
    public void accept(MeteoVisitor visitor) {
        visitor.visitMeteoFrance(this);
    }

    @Override
    public Meteo getMeteoData(double latitude, double longitude, Date date) {
        throw new NotImplementedException("No Meteo data available in MeteoFrance");
    }

    @Override
    public Integer getBera(String massifName) {
        // to be implemented
        return null;
    }
}
