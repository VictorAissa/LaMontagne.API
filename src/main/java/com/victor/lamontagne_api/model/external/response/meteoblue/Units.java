package com.victor.lamontagne_api.model.external.response.meteoblue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Units {
    private String cape;
    private String probability;
    private String windspeed;
    private String temperature;
    private String predictability;
    @JsonProperty("precipitation_probability")
    private String precipitationProbability;
    private String cloudwater;
    private String freezinglevelheight;
    private String helicity;
    private String precipitation;
    private String time;
    private String pressure;
    private String convectiveinhibition;
    private String relativehumidity;
    private String boundarylayerheight;
    private String cloudice;
    private String winddirection;
}
