package com.victor.lamontagne_api.model.external.response.meteoblue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metadata {
    @JsonProperty("modelrun_updatetime_utc")
    private String modelrunUpdatetimeUtc;
    private String name;
    private Integer height;
    @JsonProperty("timezone_abbrevation")
    private String timezoneAbbrevation;
    private Double latitude;
    @JsonProperty("modelrun_utc")
    private String modelrunUtc;
    private Double longitude;
    @JsonProperty("utc_timeoffset")
    private Double utcTimeoffset;
    @JsonProperty("generation_time_ms")
    private Double generationTimeMs;
}
