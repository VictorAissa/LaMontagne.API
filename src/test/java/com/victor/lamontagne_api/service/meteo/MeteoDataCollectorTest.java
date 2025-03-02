package com.victor.lamontagne_api.service.meteo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Date;

import com.victor.lamontagne_api.model.enums.Direction;
import com.victor.lamontagne_api.model.enums.Sky;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.victor.lamontagne_api.model.pojo.GeoPoint;
import com.victor.lamontagne_api.model.pojo.Iso;
import com.victor.lamontagne_api.model.pojo.Itinerary;
import com.victor.lamontagne_api.model.pojo.Journey;
import com.victor.lamontagne_api.model.pojo.Meteo;
import com.victor.lamontagne_api.model.pojo.Temperature;
import com.victor.lamontagne_api.model.pojo.Wind;

@ExtendWith(MockitoExtension.class)
public class MeteoDataCollectorTest {

    @Mock
    private MeteoBlueProvider meteoBlueProvider;

    @Mock
    private MeteoFranceProvider meteoFranceProvider;

    private Journey testJourney;
    private Date testDate;
    private double testLat = 45.123;
    private double testLon = 6.456;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        testJourney = createTestJourney();
    }

    @Test
    void testVisitMeteoblue() {
        Meteo meteoblueMeteo = createTestMeteo(Sky.SUNNY, 5, -10, Direction.N, 30, 2);

        when(meteoBlueProvider.getMeteoData(testLat, testLon, testDate)).thenReturn(meteoblueMeteo);

        MeteoDataCollector collector = new MeteoDataCollector(testLat, testLon, testDate);

        collector.visitMeteoblue(meteoBlueProvider);

        verify(meteoBlueProvider).getMeteoData(testLat, testLon, testDate);

        Meteo result = collector.getMeteo();
        assertNotNull(result);
        assertEquals(Sky.SUNNY, result.getSky());
        assertEquals(5, result.getTemperature().getMax());
        assertEquals(-10, result.getTemperature().getMin());
        assertEquals(Direction.N, result.getWind().getDirection());
        assertEquals(30, result.getWind().getSpeed());
        assertEquals(2, result.getBera());
    }

    @Test
    void testVisitMeteoFrance_WithExistingMeteo() {
        Meteo initialMeteo = createTestMeteo(Sky.CLOUDY, 0, -15, Direction.S, 25, 0);

        when(meteoBlueProvider.getMeteoData(testLat, testLon, testDate)).thenReturn(initialMeteo);

        when(meteoFranceProvider.getBera(any(GeoPoint.class), eq(testDate))).thenReturn(4);

        MeteoDataCollector collector = new MeteoDataCollector(testLat, testLon, testDate);

        collector.visitMeteoblue(meteoBlueProvider);

        assertNotNull(collector.getMeteo());
        assertEquals(Sky.CLOUDY, collector.getMeteo().getSky());

        collector.visitMeteoFrance(meteoFranceProvider);

        verify(meteoFranceProvider).getBera(any(GeoPoint.class), eq(testDate));

        Meteo result = collector.getMeteo();
        assertNotNull(result);
        assertEquals(4, result.getBera());

        assertEquals(Sky.CLOUDY, result.getSky());
        assertEquals(0, result.getTemperature().getMax());
        assertEquals(-15, result.getTemperature().getMin());
    }

    @Test
    void testVisitMeteoFrance_WithoutExistingMeteo() {
        when(meteoFranceProvider.getBera(any(GeoPoint.class), eq(testDate))).thenReturn(3);

        MeteoDataCollector collector = new MeteoDataCollector(testLat, testLon, testDate);

        collector.visitMeteoFrance(meteoFranceProvider);

        verify(meteoFranceProvider).getBera(any(GeoPoint.class), eq(testDate));
    }

    @Test
    void testVisitMeteoFrance_NullBera() {
        Meteo initialMeteo = createTestMeteo(Sky.SUNNY, 5, -10, Direction.N, 30, 2);

        when(meteoBlueProvider.getMeteoData(testLat, testLon, testDate)).thenReturn(initialMeteo);

        when(meteoFranceProvider.getBera(any(GeoPoint.class), eq(testDate))).thenReturn(null);

        MeteoDataCollector collector = new MeteoDataCollector(testLat, testLon, testDate);
        collector.visitMeteoblue(meteoBlueProvider);

        assertNotNull(collector.getMeteo());
        int initialBera = collector.getMeteo().getBera();

        collector.visitMeteoFrance(meteoFranceProvider);

        assertEquals(initialBera, collector.getMeteo().getBera());
    }

    @Test
    void testConstructorWithJourney() {
        Meteo meteoblueMeteo = createTestMeteo(Sky.SUNNY, 5, -10, Direction.E, 20, 2);
        when(meteoBlueProvider.getMeteoData(anyDouble(), anyDouble(), any(Date.class))).thenReturn(meteoblueMeteo);

        when(meteoFranceProvider.getBera(any(GeoPoint.class), any(Date.class))).thenReturn(3);

        MeteoDataCollector collector = new MeteoDataCollector(testJourney);

        collector.visitMeteoblue(meteoBlueProvider);
        collector.visitMeteoFrance(meteoFranceProvider);

        verify(meteoBlueProvider).getMeteoData(
                testJourney.getItinerary().getStart().getLatitude(),
                testJourney.getItinerary().getStart().getLongitude(),
                testJourney.getDate()
        );

        verify(meteoFranceProvider).getBera(
                any(GeoPoint.class),
                eq(testJourney.getDate())
        );

        Meteo result = collector.getMeteo();
        assertNotNull(result);
        assertEquals(3, result.getBera());
    }

    private Journey createTestJourney() {
        Journey journey = new Journey();
        journey.setId("test-journey-id");
        journey.setTitle("Test Journey");
        journey.setDate(testDate);

        Itinerary itinerary = new Itinerary();
        GeoPoint start = new GeoPoint();
        start.setLatitude(testLat);
        start.setLongitude(testLon);
        itinerary.setStart(start);

        GeoPoint end = new GeoPoint();
        end.setLatitude(45.234);
        end.setLongitude(6.567);
        itinerary.setEnd(end);

        journey.setItinerary(itinerary);

        return journey;
    }

    private Meteo createTestMeteo(Sky sky, int tempTop, int tempBottom, Direction windDir, int windSpeed, int bera) {
        Temperature temperature = new Temperature(tempTop, tempBottom);
        Iso iso = new Iso(2000, 3000);
        Wind wind = new Wind(windDir, windSpeed);
        return new Meteo(sky, temperature, iso, wind, bera);
    }
}