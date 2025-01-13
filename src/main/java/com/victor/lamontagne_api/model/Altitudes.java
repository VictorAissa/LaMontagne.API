package com.victor.lamontagne_api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class Altitudes {
    Integer max;
    Integer min;
    Integer total;
}
