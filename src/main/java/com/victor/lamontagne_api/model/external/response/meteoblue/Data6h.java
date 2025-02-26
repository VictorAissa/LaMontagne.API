package com.victor.lamontagne_api.model.external.response.meteoblue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Data6h {
    private List<String> time;
    @JsonProperty("liftedindex_max")
    private List<Double> liftedindexMax;
    @JsonProperty("wetbulbglobetemperature_min")
    private List<Double> wetbulbglobetemperatureMin;
    @JsonProperty("wetbulbglobetemperature_mean")
    private List<Double> wetbulbglobetemperatureMean;
    @JsonProperty("cape_mean")
    private List<Integer> capeMean;
    @JsonProperty("thunderstorm_probability")
    private List<Integer> thunderstormProbability;
    @JsonProperty("windchill_mean")
    private List<Double> windchillMean;
    @JsonProperty("convectiveupdraftvelocity_mean")
    private List<Double> convectiveupdraftvelocityMean;
    @JsonProperty("k_index_mean")
    private List<Double> kIndexMean;
    @JsonProperty("cloudice_max")
    private List<Double> cloudiceMax;
    @JsonProperty("convectivecloudbase_pressure_min")
    private List<Integer> convectivecloudbasePressureMin;
    @JsonProperty("helicity_mean")
    private List<Integer> helicityMean;
    @JsonProperty("convectiveinhibition_max")
    private List<Double> convectiveinhibitionMax;
    @JsonProperty("freezinglevelheight_min")
    private List<Double> freezinglevelheightMin;
    @JsonProperty("cloudice_min")
    private List<Double> cloudiceMin;
    @JsonProperty("freezinglevelheight_mean")
    private List<Double> freezinglevelheightMean;
    @JsonProperty("convectivecloudtop_pressure_min")
    private List<Integer> convectivecloudtopPressureMin;
    @JsonProperty("convectivecloudbase_pressure_mean")
    private List<Integer> convectivecloudbasePressureMean;
    @JsonProperty("convectiveupdraftvelocity_max")
    private List<Double> convectiveupdraftvelocityMax;
    @JsonProperty("heatindex_min")
    private List<Double> heatindexMin;
    @JsonProperty("cloudwater_max")
    private List<Double> cloudwaterMax;
    @JsonProperty("helicity_min")
    private List<Integer> helicityMin;
    @JsonProperty("cape_max")
    private List<Integer> capeMax;
    @JsonProperty("k_index_min")
    private List<Double> kIndexMin;
    @JsonProperty("boundarylayerheight_mean")
    private List<Integer> boundarylayerheightMean;
    @JsonProperty("heatindex_max")
    private List<Double> heatindexMax;
    @JsonProperty("cloudice_mean")
    private List<Double> cloudiceMean;
    @JsonProperty("cloudwater_min")
    private List<Double> cloudwaterMin;
    @JsonProperty("convectiveinhibition_mean")
    private List<Double> convectiveinhibitionMean;
    @JsonProperty("freezinglevelheight_max")
    private List<Double> freezinglevelheightMax;
    @JsonProperty("heatindex_mean")
    private List<Double> heatindexMean;
    @JsonProperty("wetbulbglobetemperature_max")
    private List<Double> wetbulbglobetemperatureMax;
    @JsonProperty("liftedindex_min")
    private List<Double> liftedindexMin;
    @JsonProperty("convectiveinhibition_min")
    private List<Double> convectiveinhibitionMin;
    @JsonProperty("convectivecloudbase_pressure_max")
    private List<Integer> convectivecloudbasePressureMax;
    @JsonProperty("convectiveupdraftvelocity_min")
    private List<Double> convectiveupdraftvelocityMin;
    @JsonProperty("k_index_max")
    private List<Double> kIndexMax;
    @JsonProperty("helicity_max")
    private List<Integer> helicityMax;
    @JsonProperty("windchill_min")
    private List<Double> windchillMin;
    @JsonProperty("cape_min")
    private List<Integer> capeMin;
    @JsonProperty("cloudwater_mean")
    private List<Double> cloudwaterMean;
    @JsonProperty("convectivecloudtop_pressure_max")
    private List<Integer> convectivecloudtopPressureMax;
    @JsonProperty("boundarylayerheight_max")
    private List<Integer> boundarylayerheightMax;
    @JsonProperty("windchill_max")
    private List<Double> windchillMax;
    @JsonProperty("boundarylayerheight_min")
    private List<Integer> boundarylayerheightMin;
    @JsonProperty("liftedindex_mean")
    private List<Double> liftedindexMean;
    @JsonProperty("convectivecloudtop_pressure_mean")
    private List<Integer> convectivecloudtopPressureMean;
}
