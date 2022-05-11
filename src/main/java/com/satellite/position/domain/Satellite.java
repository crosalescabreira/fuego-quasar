package com.satellite.position.domain;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Builder
@Data
public class Satellite  {

    String name;
    double distance;
    List<String> message;
    Position position;

    public static Satellite toConfig(HashMap<String, Object> map) {
        return Satellite.builder()
                .name(map.get("name").toString())
                .position(Position.toConfig((HashMap<String, Object>)map.get("position")))
                .build();
    }
}
