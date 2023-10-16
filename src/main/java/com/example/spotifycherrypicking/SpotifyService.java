package com.example.spotifycherrypicking;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class SpotifyService {
    private final WebClient webClient;

    SpotifyService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String fetchPlaylists() {
        Object spotify = webClient
                .get()
                .uri("https://api.spotify.com/v1/playlists/6ZD8Sy1bjSOJTwBhnekrUl")
                .attributes(clientRegistrationId("spotify"))
                .retrieve()
                .bodyToMono(Object.class).block();

        return spotify.toString();
    }
}
