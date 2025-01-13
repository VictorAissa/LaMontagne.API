package com.victor.lamontagne_api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class Itinerary {
        GeoPoint start;
        GeoPoint end;
        String gpx;
}
