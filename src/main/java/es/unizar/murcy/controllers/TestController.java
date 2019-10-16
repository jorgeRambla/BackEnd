package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.model.HelloWorld;
import es.unizar.murcy.service.HelloWorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    HelloWorldService helloWorldService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity helloWorld() throws JsonProcessingException {
        HelloWorld helloWorld = new HelloWorld("Hello world - " + UUID.randomUUID().toString());

        helloWorldService.createHelloWorld(helloWorld);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(new ObjectMapper().writeValueAsString(helloWorld));
    }
}