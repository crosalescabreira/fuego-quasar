package com.satellite.position.service;

import com.satellite.position.domain.Position;
import com.satellite.position.domain.Satellite;
import com.satellite.position.exception.BeanException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PositionService implements IPositionService {

    @Override
    public Position getLocaction(List<Position> positions, List<Double> distances) {
        try{
            double top = 0;
            double bot = 0;

            List<Satellite> satellites = new ArrayList<>();
            satellites.add(Satellite.builder().position(positions.get(0)).distance(distances.get(0)).build());
            satellites.add(Satellite.builder().position(positions.get(1)).distance(distances.get(1)).build());
            satellites.add(Satellite.builder().position(positions.get(2)).distance(distances.get(2)).build());

            for (int i = 0; i < 3; i++) {
                Satellite c = satellites.get(i);
                Satellite c2, c3;
                if (i == 0) {
                    c2 = satellites.get(1);
                    c3 = satellites.get(2);
                } else if (i == 1) {
                    c2 = satellites.get(0);
                    c3 = satellites.get(2);
                } else {
                    c2 = satellites.get(0);
                    c3 = satellites.get(1);
                }
                double d = c2.getPosition().getX() - c3.getPosition().getX();
                double v1 = (c.getPosition().getX() * c.getPosition().getX() + c.getPosition().getY() * c.getPosition().getY()) - (c.getDistance() * c.getDistance());
                top += d * v1;
                double v2 = c.getPosition().getY() * d;
                bot += v2;
            }
            double y = top / (2 * bot);
            Satellite c1 = satellites.get(0);
            Satellite c2 = satellites.get(1);
            top = c2.getDistance() * c2.getDistance() + c1.getPosition().getX() * c1.getPosition().getX() + c1.getPosition().getY() * c1.getPosition().getY() - c1.getDistance() * c1.getDistance() - c2.getPosition().getX() * c2.getPosition().getX() - c2.getPosition().getY() * c2.getPosition().getY() - 2 * (c1.getPosition().getY() - c2.getPosition().getY()) * y;
            bot = c1.getPosition().getX() - c2.getPosition().getX();
            double x = top / (2 * bot);
            return  Position.builder().x(x).y(y).build();
        }catch (Exception e){
            throw new BeanException("Error getLocaction");
        }
    }
}