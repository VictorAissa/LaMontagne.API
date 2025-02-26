package com.victor.lamontagne_api.model.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.victor.lamontagne_api.model.enums.Sky;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
public class Meteo {
    Sky sky;
    Temperature temperature;
    Iso iso;
    Wind wind;
    Integer bera;
}
