package com.example.spotifycherrypicking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("cli")
public class CliWebClientConfig {

    @Bean
    WebClient spotifyWebClient(@Value("${spotify.access-token}") String accessToken) {
        return WebClient.builder()
                .baseUrl("https://api.spotify.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }
}
