package com.satellite.position.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.satellite.position.cache.SatellitesSingleton;
import com.satellite.position.domain.Position;
import com.satellite.position.domain.Satellite;
import com.satellite.position.domain.TopSecretRequest;
import com.satellite.position.exception.BeanException;
import com.satellite.position.service.MessageService;
import com.satellite.position.service.PositionService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/topsecret_split")
public final class UseCaseTopSecretSplit {
    UseCaseTopSecretSplit(MessageService messageService, PositionService positionService, @Value("${satellite.map}") String satelliteListConfig){
        this.messageService = messageService;
        this.positionService = positionService;
        this.satelliteListConfig = satelliteListConfig;
    }

    MessageService messageService;
    PositionService positionService;
    String satelliteListConfig;

    @PostMapping()
    public ResponseEntity<String>  topsecret(@RequestBody Satellite request) {
        log.info("Call Post /api/v1/topsecret_split {}", request);
        ResponseEntity<String> response  = this.executePost(request);
        log.info("Response: {}", response);
        return response;
    }

    @GetMapping()
    public ResponseEntity<Object>  topsecret() {
        log.info("Call Get /api/v1/topsecret_split {}");
        ResponseEntity<Object> response = this.executeGet();
        log.info("Response: {}", response);
        return response;
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

    private ResponseEntity<String> executePost(Satellite satellite){
        try {
            SatellitesSingleton satellitesSingleton = SatellitesSingleton.getSatellitesSingleton();
            List<Satellite> satelliteList = new ArrayList<>();
            if(satellitesSingleton.getSatelliteList()!=null)
                satelliteList = satellitesSingleton.getSatelliteList().stream().filter(item -> !item.getName().equalsIgnoreCase(satellite.getName())).collect(Collectors.toList());
            satelliteList.add(satellite);
            satellitesSingleton.setSatelliteList(satelliteList);
            return ResponseEntity.status(HttpStatus.CREATED).body("registra satelelite") ;
        }catch(Exception ex) {
            log.error("Error registro Satelite", ex);
            throw new BeanException("Error registro Satelite");
        }
    }

    private ResponseEntity<Object> executeGet(){
        try {
            SatellitesSingleton satellitesSingleton = SatellitesSingleton.getSatellitesSingleton();
            List<Satellite> satelliteList = satellitesSingleton.getSatelliteList();

            satelliteList = satelliteList.stream().sorted(Comparator.comparing(item -> item.getName())).collect(Collectors.toList());
            //evaluacion de mensaje secreto
            List<List<String>> messages = new ArrayList<>();
            satelliteList.stream().forEach(item -> messages.add(item.getMessage()));
            String message = messageService.getMessage(messages);

            //calculo posicion
            List<Satellite> config = getSatellitesConfig(satelliteListConfig);
            config = config.stream().sorted(Comparator.comparing(item -> item.getName())).collect(Collectors.toList());
            List<Position> positions = new ArrayList<>();
            List<Double> distances = new ArrayList<>();
            config.stream().forEach(item -> positions.add(item.getPosition()));
            satelliteList.stream().forEach(item -> distances.add(item.getDistance()));
            Position position = positionService.getLocaction(positions, distances);

            return  ResponseEntity.status(HttpStatus.OK).body(ResponseView.toView(position, message));
        }catch(Exception ex) {
            log.error("Error realizando calculos", ex);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("No hay suficiente inforamcion");
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
