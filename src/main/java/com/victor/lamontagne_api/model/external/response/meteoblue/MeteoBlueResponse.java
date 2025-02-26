package com.victor.lamontagne_api.model.external.response.meteoblue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeteoBlueResponse {
    private Metadata metadata;
    private Units units;
    @JsonProperty("data_6h")
    private Data6h data6h;
    @JsonProperty("data_day")
    private DataDay dataDay;
}
