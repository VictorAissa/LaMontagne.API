package com.victor.lamontagne_api.service.meteo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.victor.lamontagne_api.model.pojo.GeoPoint;

class MassifCodeFinderTest {

    private MassifCodeFinder massifCodeFinder;

    @BeforeEach
    void setUp() {
        massifCodeFinder = new MassifCodeFinder();
        massifCodeFinder.setGeojsonFile("/data/test-massifs.geojson");
        massifCodeFinder.init();
    }

    @Test
    void testFindMassifCode_InsideQueyras() {
        GeoPoint pointInQueyras = new GeoPoint();
        pointInQueyras.setLatitude(44.65);
        pointInQueyras.setLongitude(6.75);

        Integer result = massifCodeFinder.findMassifCode(pointInQueyras);

        assertEquals(1, result);
    }

    @Test
    void testFindMassifCode_InsideEcrins() {
        GeoPoint pointInEcrins = new GeoPoint();
        pointInEcrins.setLatitude(44.85);
        pointInEcrins.setLongitude(6.25);

        Integer result = massifCodeFinder.findMassifCode(pointInEcrins);

        assertEquals(2, result);
    }

    @Test
    void testFindMassifCode_OutsideAnyMassif() {
        GeoPoint pointOutside = new GeoPoint();
        pointOutside.setLatitude(45.0);
        pointOutside.setLongitude(7.0);

        Integer result = massifCodeFinder.findMassifCode(pointOutside);

        assertNull(result);
    }
}