package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    @GetMapping("/example")
    @Operation(summary = "Пример запроса", description = "Возвращает пример сообщения")
    public String getExample() {
        return "Hello, Swagger!";
    }
}
