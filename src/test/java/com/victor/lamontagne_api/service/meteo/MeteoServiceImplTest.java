package com.victor.lamontagne_api.service.meteo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import com.victor.lamontagne_api.model.enums.Direction;
import com.victor.lamontagne_api.model.enums.Sky;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.victor.lamontagne_api.exception.ResourceNotFoundException;
import com.victor.lamontagne_api.model.pojo.GeoPoint;
import com.victor.lamontagne_api.model.pojo.Iso;
import com.victor.lamontagne_api.model.pojo.Itinerary;
import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;
import com.victor.lamontagne_api.model.pojo.Temperature;
import com.victor.lamontagne_api.model.pojo.Wind;
import com.victor.lamontagne_api.repository.JourneyRepository;

@ExtendWith(MockitoExtension.class)
public class MeteoServiceImplTest {

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private MeteoBlueProvider meteoBlueProvider;

    @Mock
    private MeteoFranceProvider meteoFranceProvider;

    @Mock
    private MeteoDataCollector mockCollector;

    private MeteoServiceImpl meteoService;

    private Journey testJourney;
    private Meteo testMeteo;
    private String journeyId = "test-journey-id";

    @BeforeEach
    void setUp() {
        meteoService = new MeteoServiceImpl(
                Arrays.asList(meteoBlueProvider, meteoFranceProvider),
                journeyRepository
        );

        testJourney = createTestJourney();
        testMeteo = createTestMeteo();
        testJourney.setMeteo(testMeteo);
    }

    @Test
    void testGetMeteoData_ExistingJourney() {
        when(journeyRepository.findById(journeyId)).thenReturn(Optional.of(testJourney));

        testJourney.setDate(Date.from(LocalDate.now().minusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        MeteoServiceImpl spyMeteoService = spy(meteoService);

        Meteo result = spyMeteoService.getMeteoData(journeyId);

        assertNotNull(result);
        assertEquals(testMeteo, result);

        verify(spyMeteoService, never()).refreshMeteoData(anyString());
    }

    @Test
    void testGetMeteoData_FutureJourney() {
        testJourney.setDate(Date.from(LocalDate.now().plusDays(2)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        when(journeyRepository.findById(journeyId)).thenReturn(Optional.of(testJourney));

        Meteo updatedMeteo = createTestMeteo();
        updatedMeteo.setBera(4);

        MeteoServiceImpl spyMeteoService = spy(meteoService);

        doReturn(updatedMeteo).when(spyMeteoService).refreshMeteoData(journeyId);

        Meteo result = spyMeteoService.getMeteoData(journeyId);

        assertNotNull(result);
        assertEquals(4, result.getBera());

        verify(spyMeteoService).refreshMeteoData(journeyId);
    }

    @Test
    void testGetMeteoData_NotFound() {
        when(journeyRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            meteoService.getMeteoData("non-existent");
        });
    }

    @Test
    void testGetMeteoData_ByCoordinates() {
        double lat = 45.123;
        double lon = 6.456;
        Date date = new Date();

        // Créer une météo de test
        Meteo testMeteo = createTestMeteo();

        // Exécuter la méthode à tester
        MeteoDataCollector realCollector = new MeteoDataCollector(lat, lon, date);
        meteoService.providers.forEach(provider -> provider.accept(realCollector));

        // Vérifier que les providers ont été appelés
        verify(meteoBlueProvider).accept(any(MeteoDataCollector.class));
        verify(meteoFranceProvider).accept(any(MeteoDataCollector.class));
    }
    @Test
    void testRefreshMeteoData() {
        when(journeyRepository.findById(journeyId)).thenReturn(Optional.of(testJourney));

        Meteo updatedMeteo = createTestMeteo();
        updatedMeteo.setBera(5);

        MeteoServiceImpl spyMeteoService = spy(meteoService);

        doReturn(updatedMeteo).when(spyMeteoService).getMeteoData(
                anyDouble(), anyDouble(), any(Date.class)
        );

        Meteo result = spyMeteoService.refreshMeteoData(journeyId);

        assertNotNull(result);
        assertEquals(5, result.getBera());

        assertEquals(updatedMeteo, testJourney.getMeteo());
        verify(journeyRepository).save(testJourney);
    }

    private Journey createTestJourney() {
        Journey journey = new Journey();
        journey.setId(journeyId);
        journey.setTitle("Test Journey");

        journey.setDate(Date.from(LocalDate.now().plusDays(3)
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Itinerary itinerary = new Itinerary();
        GeoPoint start = new GeoPoint();
        start.setLatitude(45.123);
        start.setLongitude(6.456);
        itinerary.setStart(start);

        GeoPoint end = new GeoPoint();
        end.setLatitude(45.234);
        end.setLongitude(6.567);
        itinerary.setEnd(end);

        journey.setItinerary(itinerary);

        return journey;
    }

    private Meteo createTestMeteo() {
        Temperature temperature = new Temperature(10, -5);
        Iso iso = new Iso(2000, 3000);
        Wind wind = new Wind(Direction.E, 20);
        return new Meteo(Sky.SUNNY, temperature, iso, wind, 3);
    }
}