package com.satellite.position.domain;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class Position {
    double x;
    double y;

    public static Position toConfig(HashMap<String, Object> map) {
        return Position.builder()
                .x(Double.parseDouble(map.get("x").toString()))
                .y(Double.parseDouble(map.get("y").toString()))
                .build();
    }
}
