package com.victor.lamontagne_api.model.external.response.meteoblue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataDay {
    private List<String> time;
    @JsonProperty("temperature_instant")
    private List<Double> temperatureInstant;
    private List<Double> precipitation;
    private List<Integer> predictability;
    @JsonProperty("temperature_max")
    private List<Double> temperatureMax;
    @JsonProperty("sealevelpressure_mean")
    private List<Integer> sealevelpressureMean;
    @JsonProperty("windspeed_mean")
    private List<Double> windspeedMean;
    @JsonProperty("precipitation_hours")
    private List<Double> precipitationHours;
    @JsonProperty("sealevelpressure_min")
    private List<Integer> sealevelpressureMin;
    private List<Integer> pictocode;
    private List<Double> snowfraction;
    @JsonProperty("humiditygreater90_hours")
    private List<Double> humiditygreater90Hours;
    @JsonProperty("convective_precipitation")
    private List<Double> convectivePrecipitation;
    @JsonProperty("relativehumidity_max")
    private List<Integer> relativehumidityMax;
    @JsonProperty("temperature_min")
    private List<Double> temperatureMin;
    private List<Integer> winddirection;
    @JsonProperty("felttemperature_max")
    private List<Double> felttemperatureMax;
    @JsonProperty("relativehumidity_min")
    private List<Integer> relativehumidityMin;
    @JsonProperty("felttemperature_mean")
    private List<Double> felttemperatureMean;
    @JsonProperty("windspeed_min")
    private List<Double> windspeedMin;
    @JsonProperty("felttemperature_min")
    private List<Double> felttemperatureMin;
    @JsonProperty("precipitation_probability")
    private List<Integer> precipitationProbability;
    private List<Integer> uvindex;
    private List<String> rainspot;
    @JsonProperty("temperature_mean")
    private List<Double> temperatureMean;
    @JsonProperty("sealevelpressure_max")
    private List<Integer> sealevelpressureMax;
    @JsonProperty("relativehumidity_mean")
    private List<Integer> relativehumidityMean;
    @JsonProperty("indexto6hvalues_end")
    private List<Integer> indexto6hvaluesEnd;
    @JsonProperty("predictability_class")
    private List<Double> predictabilityClass;
    @JsonProperty("windspeed_max")
    private List<Double> windspeedMax;
    @JsonProperty("indexto6hvalues_start")
    private List<Integer> indexto6hvaluesStart;
}
