package com.example.spotifycherrypicking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class SpotifyCherryPickingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotifyCherryPickingApplication.class, args);
    }
}

