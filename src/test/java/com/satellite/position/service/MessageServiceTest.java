package com.satellite.position.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageService messageService;
    @Test
    void getMessage() {
        List<List<String>> messages = new ArrayList<List<String>>();
        String [] messages1 = {"este", "", "", "mensaje", ""};
        String [] messages2 = {"es", "", "", "secreto"};
        String [] messages3 = {"este", "", "un", "", ""};
        messages.add(Arrays.stream(messages1).collect(Collectors.toList()));
        messages.add(Arrays.stream(messages2).collect(Collectors.toList()));
        messages.add(Arrays.stream(messages3).collect(Collectors.toList()));
        String message = messageService.getMessage(messages);
        String expectedMsg = "este es un mensaje secreto";
        assertEquals(expectedMsg,message);
    }
}