package com.satellite.position.service;

import com.satellite.position.domain.Position;

import java.util.List;

public interface IPositionService {
    public Position getLocaction(List<Position> positions, List<Double> distances);
}
