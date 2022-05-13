package com.satellite.position.usecases;

import com.satellite.position.domain.Position;
import com.satellite.position.domain.Satellite;
import com.satellite.position.domain.TopSecretRequest;
import com.satellite.position.service.MessageService;
import com.satellite.position.service.PositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UseCaseTopSecretTest {

    @Test
    @DisplayName("UseCaseTopSecretTest test Ok")
    void topsecret() {
        //given
        var expectedResponse = expectedResponse();

        MessageService messageService = mock(MessageService.class);
        PositionService positionService = mock(PositionService.class);
        String satelliteListConfig = "[{\"name\":\"Kenobi\",\"position\":{ \"x\":-500, \"y\":-200}},{\"name\":\"Skywalker\",\"position\":{ \"x\":100, \"y\":-100}},{\"name\":\"Sato\",\"position\":{ \"x\":500, \"y\":100}}]";
        //when
        when(messageService.getMessage(any())).thenReturn("este es un mensaje secreto");
        when(positionService.getLocaction(any(),any())).thenReturn(Position.builder().x(-164.338305).y(480.5222).build());

        var useCase = new UseCaseTopSecret(messageService,positionService, satelliteListConfig);
        var actualResponse = useCase.topsecret(request());

        //then
        assertEquals(expectedResponse, actualResponse);
    }

    private TopSecretRequest request(){
        return TopSecretRequest.builder()
                .satellites(
                        List.of(
                                Satellite.builder().name("kenobi").distance(100.0).message(List.of( "", "es", "", "", "secreto")).build(),
                                Satellite.builder().name("skywalker").distance(115.5).message(List.of(  "", "es", "", "", "secreto")).build(),
                                Satellite.builder().name("sato").distance(142.7).message(List.of( "este", "", "un", "", "")).build()
                        )
                )
                .build();
    }


    static UseCaseTopSecret.ResponseView expectedResponse(){
        return UseCaseTopSecret.ResponseView.builder()
                .position(UseCaseTopSecret.PositionView.builder().x(-164.338305).y(480.5222).build())
                .message( "este es un mensaje secreto")
                .build();
    }
}