package com.web.stard.domain.test.api;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Hidden
    @GetMapping("/health")
    public String healthCheck() {
        return "I'm healthy";
    }
}