package com.victor.lamontagne_api.model.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class Temperature {
    Integer top;
    Integer bottom;
}
