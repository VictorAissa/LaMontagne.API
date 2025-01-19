package com.victor.lamontagne_api.model.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class Wind {
    Direction direction;
    Integer speed;
}
