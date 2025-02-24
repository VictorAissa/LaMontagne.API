package com.victor.lamontagne_api.model.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonSerialize
@AllArgsConstructor
public class Iso {
    Integer night;
    Integer day;
}
