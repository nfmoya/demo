package com.example.demo.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @RequestMapping("/demo")
    public ResponseEntity helloWorld() {

        return new ResponseEntity<>("Hello World DEMO!", HttpStatus.OK);
    }

}
