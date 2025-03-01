package com.victor.lamontagne_api.service.meteo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victor.lamontagne_api.model.pojo.GeoPoint;
import jakarta.annotation.PostConstruct;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class MassifCodeFinder {

    private final List<MassifInfo> massifs = new ArrayList<>();
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @PostConstruct
    public void init() {
        try {
            loadMassifsFromGeoJson();
        } catch (Exception e) {
            throw new RuntimeException("error in massifs loading", e);
        }
    }

    public Integer findMassifCode(GeoPoint point) {
        Point geoPoint = geometryFactory.createPoint(new Coordinate(point.getLongitude(), point.getLatitude()));

        for (MassifInfo massif : massifs) {
            if (massif.getGeometry().contains(geoPoint)) {
                return massif.getCode();
            }
        }

        return null;
    }

    private void loadMassifsFromGeoJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = new ClassPathResource("/data/Metadata_massif_DP_2024_20240215.geojson");

        if (!resource.exists()) {
            throw new IOException("GeoJSON file not found: data/Metadata_massif_DP_2024_20240215.geojson");
        }

        try (InputStream inputStream = resource.getInputStream()) {

            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode features = rootNode.path("features");

            for (JsonNode feature : features) {
                Integer code = feature.path("properties").path("code").asInt();
                String title = feature.path("properties").path("title").asText();

                JsonNode geometry = feature.path("geometry");
                String geometryType = geometry.path("type").asText();

                if ("MultiPolygon".equals(geometryType)) {
                    JsonNode coordinates = geometry.path("coordinates");
                    List<Polygon> polygons = new ArrayList<>();

                    for (JsonNode polygonCoords : coordinates) {
                        for (JsonNode ring : polygonCoords) {
                            Coordinate[] coords = new Coordinate[ring.size()];

                            for (int i = 0; i < ring.size(); i++) {
                                JsonNode point = ring.get(i);
                                double lon = point.get(0).asDouble();
                                double lat = point.get(1).asDouble();
                                coords[i] = new Coordinate(lon, lat);
                            }

                            if (!coords[0].equals2D(coords[coords.length - 1])) {
                                Coordinate[] closedCoords = new Coordinate[coords.length + 1];
                                System.arraycopy(coords, 0, closedCoords, 0, coords.length);
                                closedCoords[coords.length] = coords[0];
                                coords = closedCoords;
                            }

                            LinearRing shell = geometryFactory.createLinearRing(coords);
                            Polygon polygon = geometryFactory.createPolygon(shell, null);
                            polygons.add(polygon);
                        }
                    }

                    MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(
                            polygons.toArray(new Polygon[0]));

                    massifs.add(new MassifInfo(code, title, multiPolygon));
                }
            }
        }
    }
    //@Getter
    static class MassifInfo {
        private final Integer code;
        private final String title;
        private final Geometry geometry;

        public MassifInfo(Integer code, String title, Geometry geometry) {
            this.code = code;
            this.title = title;
            this.geometry = geometry;
        }

        public Integer getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public Geometry getGeometry() {
            return geometry;
        }
    }
}