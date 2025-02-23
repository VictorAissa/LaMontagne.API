package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeteoServiceImpl implements MeteoService {
    private final List<MeteoProvider> providers;

    @Autowired
    public MeteoServiceImpl(List<MeteoProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Meteo getMeteoData(Journey journey) {
        MeteoDataCollector collector = new MeteoDataCollector(journey);
        providers.forEach(provider -> provider.accept(collector));
        return collector.getMeteo();
    }

}
