package com.satellite.position.service;

import com.satellite.position.domain.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
class PositionServiceTest {

    @Autowired
    private PositionService positionService;
    @Test
    void getLocaction() {
        List<Position> positions = new ArrayList<>();
        positions.add(Position.builder().x(-500.0).y(-200).build());
        positions.add(Position.builder().x(100.0).y(-100).build());
        positions.add(Position.builder().x(500.0).y(100).build());
        List<Double> distances = new ArrayList<>();
        distances.add(100.0);
        distances.add(115.5);
        distances.add(142.7);

        Position expectedPosition = Position.builder().x(-45.53230833333335).y(-1093.5074).build();
        Position calculatedPosition = positionService.getLocaction(positions, distances);
        assertEquals(expectedPosition, calculatedPosition);
    }
}