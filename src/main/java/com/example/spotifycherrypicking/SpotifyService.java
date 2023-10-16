package com.example.spotifycherrypicking;

import com.example.spotifycherrypicking.model.SpotifyTrackResponse;
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
        SpotifyTrackResponse spotify = webClient
                .get()
                .uri("https://api.spotify.com/v1/me/tracks")
                .attributes(clientRegistrationId("spotify"))
                .retrieve()
                .bodyToMono(SpotifyTrackResponse.class).block();

        return "user has " + spotify.total() + " tracks";
    }
}
