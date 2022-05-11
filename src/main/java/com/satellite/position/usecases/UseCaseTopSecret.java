package com.satellite.position.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.satellite.position.domain.Position;
import com.satellite.position.domain.Satellite;
import com.satellite.position.domain.TopSecretRequest;
import com.satellite.position.exception.BeanException;
import com.satellite.position.service.MessageService;
import com.satellite.position.service.PositionService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public final class UseCaseTopSecret {

    @Autowired
    MessageService messageService;
    @Autowired
    PositionService positionService;

    @Value("${satellite.map}")
    String satelliteListConfig;

    @PostMapping("/v1/topsecret")
    public ResponseView topsecret(@RequestBody TopSecretRequest request) {
        log.info("Call /v1/topsecret {}", request);
        ResponseView view = this.execute(request.getSatellites());
        log.info("Response: {}", view);
        return view;
    }

    private List<Satellite> getSatellitesConfig(String propertyStr) {
        try {
            List<HashMap<String, Object>> list = new ObjectMapper().readValue(propertyStr, List.class);
            return list.stream().map(Satellite::toConfig).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error realizando el parse de json de una property", ex);
            throw new BeanException("Error configuracion");
        }
    }

    private ResponseView execute(List<Satellite> satelliteList){
        try {
            satelliteList = satelliteList.stream().sorted(Comparator.comparing(satellite -> satellite.getName())).collect(Collectors.toList());
            //evaluacion de mensaje secreto
            List<List<String>> messages = new ArrayList<>();
            satelliteList.stream().forEach(satellite -> messages.add(satellite.getMessage()));
            String message = messageService.getMessage(messages);

            //calculo posicion
            List<Satellite> config = getSatellitesConfig(satelliteListConfig);
            config = config.stream().sorted(Comparator.comparing(satellite -> satellite.getName())).collect(Collectors.toList());
            List<Position> positions = new ArrayList<>();
            List<Double> distances = new ArrayList<>();
            config.stream().forEach(satellite -> positions.add(satellite.getPosition()));
            satelliteList.stream().forEach(satellite -> distances.add(satellite.getDistance()));
            Position position = positionService.getLocaction(positions, distances);

            return ResponseView.toView(position, message);
        }catch(Exception ex) {
            log.error("Error realizando calculos", ex);
            throw new BeanException("Error calculos");
        }
    }

    @Builder
    @lombok.Value
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class ResponseView {

        private PositionView position;
        private String message;

        private static ResponseView toView(Position position , String message) {
            return ResponseView.builder()
                    .position(PositionView.toView(position))
                    .message(message)
                    .build();
        }
    }

    @Builder
    @lombok.Value
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class PositionView {
        double x;
        double y;

        private static PositionView toView(Position position){
            return PositionView.builder().x(position.getX()).y(position.getY()).build();
        }
    }
}
