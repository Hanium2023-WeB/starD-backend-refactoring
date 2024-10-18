package com.web.stard.domain.test.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/health")
    public String healthCheck() {
        return "I'm healthy";
    }
}