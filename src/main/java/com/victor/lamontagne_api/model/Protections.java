package com.victor.lamontagne_api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

@Data
@JsonSerialize
public class Protections {
    List<Rope> ropes;
    Integer nuts;
    List<Double> cams;
    Integer screws;
}
