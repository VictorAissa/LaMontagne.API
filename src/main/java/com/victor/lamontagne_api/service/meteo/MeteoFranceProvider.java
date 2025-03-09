package com.victor.lamontagne_api.service.meteo;

import com.victor.lamontagne_api.config.MeteoFranceConfig;
import com.victor.lamontagne_api.model.pojo.GeoPoint;
import com.victor.lamontagne_api.model.pojo.Meteo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "meteofrance.enabled", havingValue = "true", matchIfMissing = true)
public class MeteoFranceProvider implements MeteoProvider {

    private final WebClient webClient;
    private final MeteoFranceConfig meteoFranceConfig;
    private final MassifCodeFinder massifCodeFinder;
    private static final Logger logger = LoggerFactory.getLogger(MeteoFranceProvider.class);

    @Autowired
    public MeteoFranceProvider(WebClient meteoFranceWebClient,
                               MeteoFranceConfig meteoFranceConfig,
                               MassifCodeFinder massifCodeFinder) {
        this.webClient = meteoFranceWebClient;
        this.meteoFranceConfig = meteoFranceConfig;
        this.massifCodeFinder = massifCodeFinder;
    }

    @Override
    public void accept(MeteoVisitor visitor) {
        visitor.visitMeteoFrance(this);
    }

    @Override
    public Meteo getMeteoData(double latitude, double longitude, Date date) {
        throw new UnsupportedOperationException("No MeteoData available for MeteoFranceProvider");
    }

    public Integer getBera(GeoPoint point, Date date) {
        Integer massifCode = massifCodeFinder.findMassifCode(point);
        if (massifCode == null) {
            logger.warn("Point out of french known massifs: lat={}, lon={}", point.getLatitude(), point.getLongitude());
            return null;
        }

        logger.debug("Massif found: code={}", massifCode);

        Map<String, Object> beraInfo = getBeraForMassifAndDate(massifCode.toString(), date);

        if (beraInfo == null || beraInfo.containsKey("error")) {
            logger.warn("Impossible to recover BERA: massif={}, date={}", massifCode, date);
            return null;
        }

        return (Integer) beraInfo.get("risque");
    }

    @Override
    public Integer getBera(String massifId) {
        Map<String, Object> beraInfo = getBeraForMassifAndDate(massifId, new Date());

        if (beraInfo == null || beraInfo.containsKey("error")) {
            return null;
        }

        return (Integer) beraInfo.get("risque");
    }

    public Map<String, Object> getBeraForMassifAndDate(String massifId, Date date) {
        LocalDate journeyDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String beraXml = fetchBeraXml(massifId);

        if (beraXml == null || beraXml.isEmpty()) {
            logger.error("Impossible to recover BERA={}", massifId);
            return createErrorResponse("Impossible de récupérer le bulletin BERA");
        }

        try {
            Document doc = parseXml(beraXml);

            LocalDateTime dateValidite = extractDate(doc, "DateValidite");
            LocalDate dateJ = dateValidite.toLocalDate();

            String dateJ2Str = getXmlValue(doc, "//RISQUE/@DATE_RISQUE_J2");
            LocalDate dateJ2 = null;
            if (dateJ2Str != null && !dateJ2Str.isEmpty()) {
                dateJ2 = LocalDateTime.parse(dateJ2Str, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
            }

            Integer risqueJ = null;
            Integer risqueJ2 = null;
            String risqueJStr = getXmlValue(doc, "//RISQUE/@RISQUEMAXI");
            String risqueJ2Str = getXmlValue(doc, "//RISQUE/@RISQUEMAXIJ2");
            if (risqueJStr != null && !risqueJStr.isEmpty()) {
                risqueJ= Integer.parseInt(risqueJStr);
            }
            if (risqueJ2Str != null && !risqueJ2Str.isEmpty()) {
                risqueJ2 = Integer.parseInt(risqueJ2Str);
            }

            Map<String, Object> result = new HashMap<>();

            if (journeyDate.isEqual(dateJ)) {
                result.put("risque", risqueJ);
                result.put("source", "Indice BERA du jour");

                addSlopeInfo(doc, result);
                addAltitudeInfo(doc, result);
            }
            else if (dateJ2 != null && journeyDate.isEqual(dateJ2)) {
                result.put("risque", risqueJ2);
                result.put("source", "Prévision BERA à J+1");
            }
            else if (dateJ2 != null && journeyDate.isAfter(dateJ2)) {
                result.put("risque", 0);
                result.put("source", "Prévision BERA inconnue");
                result.put("warning", "Date au-delà des prévisions disponibles");
            }
            else {
                result.put("risque", null);
                result.put("source", "Pas de données disponibles pour cette date");
            }

            result.put("dateBulletin", getXmlValue(doc, "/BULLETINS_NEIGE_AVALANCHE/@DATEBULLETIN"));
            result.put("massif", getXmlValue(doc, "/BULLETINS_NEIGE_AVALANCHE/@MASSIF"));

            return result;

        } catch (Exception e) {
            logger.error("Error while parsing xml BERA: {}", e.getMessage(), e);
            return createErrorResponse("EError while parsing xml BERA: " + e.getMessage());
        }
    }

    private void addSlopeInfo(Document doc, Map<String, Object> result) {
        Map<String, Boolean> pentes = new HashMap<>();
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

        for (String dir : directions) {
            String value = getXmlValue(doc, "//PENTE/@" + dir);
            pentes.put(dir, "true".equals(value));
        }

        result.put("pentes", pentes);
    }

    private void addAltitudeInfo(Document doc, Map<String, Object> result) {
        String altitude = getXmlValue(doc, "//RISQUE/@ALTITUDE");
        if (altitude != null && !altitude.isEmpty()) {
            result.put("altitudeLimite", Integer.parseInt(altitude));

            String risque1 = getXmlValue(doc, "//RISQUE/@RISQUE1");
            String risque2 = getXmlValue(doc, "//RISQUE/@RISQUE2");

            if (risque1 != null && !risque1.isEmpty()) {
                result.put("risqueBasseAltitude", Integer.parseInt(risque1));
            }

            if (risque2 != null && !risque2.isEmpty()) {
                result.put("risqueHauteAltitude", Integer.parseInt(risque2));
            }
        }
    }

    private String fetchBeraXml(String massifId) {
        try {
            String url = meteoFranceConfig.buildBraRequestUrl(massifId);
            logger.debug("Calling MeteoFrance API with URL: {}", url);

            return webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        logger.error("Erreur lors de l'appel à l'API MétéoFrance: {}", e.getMessage(), e);
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du bulletin BERA: {}", e.getMessage(), e);
            return null;
        }
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    private LocalDateTime extractDate(Document doc, String elementName) throws Exception {
        NodeList nodes = doc.getElementsByTagName(elementName);
        if (nodes.getLength() > 0) {
            String dateStr = nodes.item(0).getTextContent();
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        }
        throw new Exception("Élément " + elementName + " non trouvé dans le XML");
    }

    private String getXmlValue(Document doc, String xpathExpression) {
        try {
            if (xpathExpression.startsWith("//RISQUE/@")) {
                NodeList risques = doc.getElementsByTagName("RISQUE");
                if (risques.getLength() > 0) {
                    Element risque = (Element) risques.item(0);
                    return risque.getAttribute(xpathExpression.replace("//RISQUE/@", ""));
                }
            } else if (xpathExpression.startsWith("//PENTE/@")) {
                NodeList pentes = doc.getElementsByTagName("PENTE");
                if (pentes.getLength() > 0) {
                    Element pente = (Element) pentes.item(0);
                    return pente.getAttribute(xpathExpression.replace("//PENTE/@", ""));
                }
            } else if (xpathExpression.startsWith("/BULLETINS_NEIGE_AVALANCHE/@")) {
                Element root = doc.getDocumentElement();
                return root.getAttribute(xpathExpression.replace("/BULLETINS_NEIGE_AVALANCHE/@", ""));
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction de la valeur XML: {}", e.getMessage(), e);
        }

        return null;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        return error;
    }
}