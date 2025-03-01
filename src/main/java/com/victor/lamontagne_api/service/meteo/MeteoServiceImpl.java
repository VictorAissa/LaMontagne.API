package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.exception.ResourceNotFoundException;
import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;
import com.victor.lamontagne_api.repository.JourneyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class MeteoServiceImpl implements MeteoService {
    private final List<MeteoProvider> providers;
    private final JourneyRepository journeyRepository;

    @Autowired
    public MeteoServiceImpl(List<MeteoProvider> providers, JourneyRepository journeyRepository) {
        this.providers = providers;
        this.journeyRepository = journeyRepository;
    }

    @Override
    public Meteo getMeteoData(String journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));

        if (journey.isFutureJourney() && shouldUpdateMeteo(journey.getDate())) {
            return refreshMeteoData(journeyId);
        }

        return journey.getMeteo();
    }

    @Override
    public Meteo getMeteoData(double latitude, double longitude, Date date) {
        MeteoDataCollector collector = new MeteoDataCollector(latitude, longitude, date);
        providers.forEach(provider -> provider.accept(collector));
        return collector.getMeteo();
    }

    @Override
    public Meteo refreshMeteoData(String journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));

        Meteo updatedMeteo = getMeteoData(
                journey.getItinerary().getStart().getLatitude(),
                journey.getItinerary().getStart().getLongitude(),
                journey.getDate()
        );

        journey.setMeteo(updatedMeteo);
        journeyRepository.save(journey);

        return updatedMeteo;
    }

    boolean shouldUpdateMeteo(Date date) {
        return ChronoUnit.DAYS.between(LocalDate.now(), date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()) <= 7;
    }
}
