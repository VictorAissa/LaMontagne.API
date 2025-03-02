package com.victor.lamontagne_api.service.meteo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.victor.lamontagne_api.config.MeteoFranceConfig;
import com.victor.lamontagne_api.model.pojo.GeoPoint;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MeteoFranceProviderTest {

    @Mock
    private WebClient webClient;

    @Mock
    private MeteoFranceConfig meteoFranceConfig;

    @Mock
    private MassifCodeFinder massifCodeFinder;

    private WebClient.ResponseSpec responseSpec;

    private MeteoFranceProvider meteoFranceProvider;
    private Date testDate;
    private GeoPoint testPoint;
    private String testMassifId = "QUEYRAS";
    private String testUrl = "https://api.meteofrance.com/test-url";

    private final String validXmlResponseJ1 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<BULLETINS_NEIGE_AVALANCHE DATEBULLETIN=\"2023-03-01\" MASSIF=\"QUEYRAS\">\n" +
                    "  <DateValidite>2023-03-01T12:00:00</DateValidite>\n" +
                    "  <CARTOUCHERISQUE>\n" +
                    "    <RISQUE RISQUEMAXI=\"3\" RISQUE1=\"2\" RISQUE2=\"3\" ALTITUDE=\"2000\" " +
                    "RISQUEMAXIJ2=\"4\" DATE_RISQUE_J2=\"2023-03-02T12:00:00\"/>\n" +
                    "    <PENTE N=\"true\" NE=\"true\" E=\"false\" SE=\"false\" S=\"false\" SW=\"false\" W=\"true\" NW=\"true\"/>\n" +
                    "  </CARTOUCHERISQUE>\n" +
                    "</BULLETINS_NEIGE_AVALANCHE>";

    @BeforeEach
    void setUp() {
        testDate = Date.from(LocalDate.of(2023, 3, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        testPoint = new GeoPoint();
        testPoint.setLatitude(44.65);
        testPoint.setLongitude(6.75);

        meteoFranceProvider = new MeteoFranceProvider(webClient, meteoFranceConfig, massifCodeFinder);
    }

    void setupWebClientMocks() {
        // Configuration de la chaîne de mocks pour WebClient
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.accept(MediaType.APPLICATION_XML)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        this.responseSpec = responseSpecMock;

        lenient().when(meteoFranceConfig.buildBraRequestUrl(anyString())).thenReturn(testUrl);
    }

    @Test
    void testGetMeteoData_ThrowsUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> {
            meteoFranceProvider.getMeteoData(testPoint.getLatitude(), testPoint.getLongitude(), testDate);
        });
    }

    @Test
    void testGetBera_WithMassifId_Success() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validXmlResponseJ1));

        Integer result = meteoFranceProvider.getBera(testMassifId);

        assertNotNull(result);
        assertEquals(4, result);

        verify(meteoFranceConfig).buildBraRequestUrl(testMassifId);
    }

    @Test
    void testGetBera_WithMassifId_EmptyResponse() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());

        Integer result = meteoFranceProvider.getBera(testMassifId);

        assertNull(result);
    }

    @Test
    void testGetBera_WithPoint_Success() {
        // Setup mocks
        setupWebClientMocks();
        when(massifCodeFinder.findMassifCode(testPoint)).thenReturn(1);  // code for QUEYRAS
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validXmlResponseJ1));

        Integer result = meteoFranceProvider.getBera(testPoint, testDate);

        assertNotNull(result);
        assertEquals(3, result);

        verify(massifCodeFinder).findMassifCode(testPoint);

        verify(meteoFranceConfig).buildBraRequestUrl(anyString());
    }

    @Test
    void testGetBera_WithPoint_MassifNotFound() {
        when(massifCodeFinder.findMassifCode(testPoint)).thenReturn(null);

        Integer result = meteoFranceProvider.getBera(testPoint, testDate);

        assertNull(result);
    }

    @Test
    void testGetBeraForMassifAndDate_SameDay() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validXmlResponseJ1));

        Date sameDayDate = Date.from(LocalDate.of(2023, 3, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> result = meteoFranceProvider.getBeraForMassifAndDate(testMassifId, sameDayDate);

        assertNotNull(result);
        assertEquals(3, result.get("risque"));
        assertEquals("Indice BERA du jour", result.get("source"));

        assertTrue(result.containsKey("pentes"));
        @SuppressWarnings("unchecked")
        Map<String, Boolean> pentes = (Map<String, Boolean>)result.get("pentes");
        assertTrue(pentes.get("N"));
        assertTrue(pentes.get("NE"));
        assertFalse(pentes.get("E"));
        assertTrue(pentes.get("W"));

        assertEquals(2000, result.get("altitudeLimite"));
        assertEquals(2, result.get("risqueBasseAltitude"));
        assertEquals(3, result.get("risqueHauteAltitude"));
    }

    @Test
    void testGetBeraForMassifAndDate_NextDay() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validXmlResponseJ1));

        Date nextDayDate = Date.from(LocalDate.of(2023, 3, 2)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> result = meteoFranceProvider.getBeraForMassifAndDate(testMassifId, nextDayDate);

        assertNotNull(result);
        assertEquals(4, result.get("risque"));
        assertEquals("Prévision BERA à J+1", result.get("source"));

        assertFalse(result.containsKey("pentes"));
        assertFalse(result.containsKey("altitudeLimite"));
    }

    @Test
    void testGetBeraForMassifAndDate_BeyondPrediction() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validXmlResponseJ1));

        Date futureDayDate = Date.from(LocalDate.of(2023, 3, 3)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> result = meteoFranceProvider.getBeraForMassifAndDate(testMassifId, futureDayDate);

        assertNotNull(result);
        assertEquals(4, result.get("risque"));
        assertTrue(result.get("source").toString().contains("prolongée"));
        assertTrue(result.containsKey("warning"));
    }

    @Test
    void testGetBeraForMassifAndDate_PastDate() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(validXmlResponseJ1));

        Date pastDayDate = Date.from(LocalDate.of(2023, 2, 28)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> result = meteoFranceProvider.getBeraForMassifAndDate(testMassifId, pastDayDate);

        assertNotNull(result);
        assertNull(result.get("risque"));
        assertTrue(result.get("source").toString().contains("disponibles"));
    }

    @Test
    void testGetBeraForMassifAndDate_EmptyResponse() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());

        Map<String, Object> result = meteoFranceProvider.getBeraForMassifAndDate(testMassifId, testDate);

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }

    @Test
    void testGetBeraForMassifAndDate_InvalidXml() {
        setupWebClientMocks();
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("<invalid>xml"));

        Map<String, Object> result = meteoFranceProvider.getBeraForMassifAndDate(testMassifId, testDate);

        assertNotNull(result);
        assertTrue(result.containsKey("error"));
    }
}