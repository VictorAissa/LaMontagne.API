package com.victor.lamontagne_api.service.meteo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.victor.lamontagne_api.config.MeteoBlueConfig;
import com.victor.lamontagne_api.model.enums.Direction;
import com.victor.lamontagne_api.model.enums.Sky;
import com.victor.lamontagne_api.model.external.response.meteoblue.Data6h;
import com.victor.lamontagne_api.model.external.response.meteoblue.DataDay;
import com.victor.lamontagne_api.model.external.response.meteoblue.MeteoBlueResponse;
import com.victor.lamontagne_api.model.pojo.Meteo;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MeteoBlueProviderTest {

    @Mock
    private WebClient webClient;

    @Mock
    private MeteoBlueConfig meteoblueConfig;

    private WebClient.ResponseSpec responseSpec;

    private MeteoBlueProvider meteoBlueProvider;
    private Date testDate;
    private double testLat = 45.123;
    private double testLon = 6.456;
    private String testUrl = "https://api.meteoblue.com/test-url";

    @BeforeEach
    void setUp() {
        testDate = Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        this.responseSpec = responseSpecMock;

        when(meteoblueConfig.buildRequestUrl(testLat, testLon)).thenReturn(testUrl);

        meteoBlueProvider = new MeteoBlueProvider(webClient, meteoblueConfig);
    }

    @Test
    void testGetMeteoData_Success() {
        MeteoBlueResponse response = createTestResponse();
        when(responseSpec.bodyToMono(MeteoBlueResponse.class)).thenReturn(Mono.just(response));

        Meteo result = meteoBlueProvider.getMeteoData(testLat, testLon, testDate);

        assertNotNull(result);
        assertEquals(Sky.SUNNY, result.getSky());
        assertEquals(10, result.getTemperature().getMax());
        assertEquals(0, result.getTemperature().getMin());
        assertEquals(Direction.NE, result.getWind().getDirection());
        assertEquals(36, result.getWind().getSpeed()); // 10 m/s * 3.6 = 36 km/h
        assertEquals(2000, result.getIso().getDay());
        assertEquals(1800, result.getIso().getNight());

        verify(webClient).get();
        verify(meteoblueConfig).buildRequestUrl(testLat, testLon);
    }

    @Test
    void testGetMeteoData_NullResponse() {
        when(responseSpec.bodyToMono(MeteoBlueResponse.class)).thenReturn(Mono.empty());

        Meteo result = meteoBlueProvider.getMeteoData(testLat, testLon, testDate);

        assertNotNull(result);
        assertEquals(Sky.PARTLY_CLOUDY, result.getSky());
        assertEquals(0, result.getTemperature().getMax());
        assertEquals(0, result.getTemperature().getMin());
        assertEquals(Direction.N, result.getWind().getDirection());
        assertEquals(0, result.getWind().getSpeed());
        assertEquals(0, result.getIso().getDay());
        assertEquals(0, result.getIso().getNight());
    }

    @Test
    void testGetMeteoData_MissingDataDay() {
        MeteoBlueResponse response = new MeteoBlueResponse();

        when(responseSpec.bodyToMono(MeteoBlueResponse.class)).thenReturn(Mono.just(response));

        Meteo result = meteoBlueProvider.getMeteoData(testLat, testLon, testDate);

        assertNotNull(result);
        assertEquals(Sky.PARTLY_CLOUDY, result.getSky());
    }

    @Test
    void testGetMeteoData_ApiException() {
        when(responseSpec.bodyToMono(MeteoBlueResponse.class)).thenReturn(
                Mono.error(new RuntimeException("API Error")));

        Meteo result = meteoBlueProvider.getMeteoData(testLat, testLon, testDate);

        assertNotNull(result);
        assertEquals(Sky.PARTLY_CLOUDY, result.getSky());
    }

    @Test
    void testMapPictocodeToSky() {
        testPictocode(1, Sky.SUNNY);
        testPictocode(2, Sky.SUNNY);
        testPictocode(3, Sky.PARTLY_CLOUDY);
        testPictocode(7, Sky.PARTLY_CLOUDY);
        testPictocode(10, Sky.PARTLY_CLOUDY);
        testPictocode(4, Sky.CLOUDY);
        testPictocode(5, Sky.CLOUDY);
        testPictocode(7, Sky.LIGHT_RAIN);
        testPictocode(12, Sky.LIGHT_RAIN);
        testPictocode(16, Sky.LIGHT_RAIN);
        testPictocode(6, Sky.HEAVY_RAIN);
        testPictocode(8, Sky.HEAVY_RAIN);
        testPictocode(14, Sky.HEAVY_RAIN);
        testPictocode(10, Sky.LIGHT_SNOW);
        testPictocode(11, Sky.LIGHT_SNOW);
        testPictocode(13, Sky.LIGHT_SNOW);
        testPictocode(17, Sky.LIGHT_SNOW);
        testPictocode(9, Sky.HEAVY_SNOW);
        testPictocode(15, Sky.HEAVY_SNOW);
        testPictocode(99, Sky.PARTLY_CLOUDY);
    }

    private Sky mapPictocodeToSky(int pictocode) {
        switch(pictocode) {
            case 1: case 2:
                return Sky.SUNNY;
            case 3:
                return Sky.PARTLY_CLOUDY;
            case 4: case 5:
                return Sky.CLOUDY;
            case 7: case 12: case 16:
                return Sky.LIGHT_RAIN;
            case 6: case 8: case 14:
                return Sky.HEAVY_RAIN;
            case 10: case 11: case 13: case 17:
                return Sky.LIGHT_SNOW;
            case 9: case 15:
                return Sky.HEAVY_SNOW;
            default:
                return Sky.PARTLY_CLOUDY;
        }
    }

    private void testPictocode(int pictocode, Sky expectedSky) {
        MeteoBlueResponse response = createTestResponse();
        response.getDataDay().getPictocode().set(0, pictocode);

        when(responseSpec.bodyToMono(MeteoBlueResponse.class)).thenReturn(Mono.just(response));

        Meteo result = meteoBlueProvider.getMeteoData(testLat, testLon, testDate);

        assertEquals(expectedSky, result.getSky());
    }

    @Test
    void testMapWindDirection() {
        testWindDirection(10, Direction.N);
        testWindDirection(45, Direction.NE);
        testWindDirection(90, Direction.E);
        testWindDirection(135, Direction.SE);
        testWindDirection(180, Direction.S);
        testWindDirection(225, Direction.SW);
        testWindDirection(270, Direction.W);
        testWindDirection(315, Direction.NW);
        testWindDirection(350, Direction.N);
    }

    private void testWindDirection(int windDirection, Direction expectedDirection) {
        MeteoBlueResponse response = createTestResponse();
        response.getDataDay().getWinddirection().set(0, windDirection);

        when(responseSpec.bodyToMono(MeteoBlueResponse.class)).thenReturn(Mono.just(response));

        Meteo result = meteoBlueProvider.getMeteoData(testLat, testLon, testDate);

        assertEquals(expectedDirection, result.getWind().getDirection());
    }

    private MeteoBlueResponse createTestResponse() {
        MeteoBlueResponse response = new MeteoBlueResponse();

        DataDay dataDay = new DataDay();
        dataDay.setPictocode(Arrays.asList(1));  // SUNNY
        dataDay.setTemperatureMax(Arrays.asList(10.0));
        dataDay.setTemperatureMin(Arrays.asList(0.0));
        dataDay.setWinddirection(Arrays.asList(45));  // NE
        dataDay.setWindspeedMean(Arrays.asList(10.0));  // 10 m/s
        dataDay.setIndexto6hvaluesStart(Arrays.asList(0));
        dataDay.setTime(Arrays.asList(LocalDate.now().toString()));

        response.setDataDay(dataDay);

        Data6h data6h = new Data6h();
        data6h.setFreezinglevelheightMean(Arrays.asList(1800.0, 1900.0, 2000.0));

        response.setData6h(data6h);

        return response;
    }
}