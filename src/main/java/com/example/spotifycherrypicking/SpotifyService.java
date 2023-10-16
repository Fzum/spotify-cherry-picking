package com.example.spotifycherrypicking;

import com.example.spotifycherrypicking.model.Item;
import com.example.spotifycherrypicking.model.SpotifyTrackResponse;
import com.example.spotifycherrypicking.model.Track;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class SpotifyService {
    private final WebClient spofifyWebClient;

    SpotifyService(WebClient spofifyWebClient) {
        this.spofifyWebClient = spofifyWebClient;
    }

    public String fetchPlaylists() {
        boolean hasNext = true;

        SpotifyTrackResponse spotify = spofifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/me/tracks")
                        .queryParam("limit", 50)
                        .queryParam("market", "AT")
                        .build())
                .retrieve()
                .bodyToMono(SpotifyTrackResponse.class)
                .block();

        final List<Track> tracks = new ArrayList<>(spotify.items().stream().map(Item::track).toList());

        if (spotify.next() != null) {
            while (hasNext) {
                spotify = spofifyWebClient
                        .get()
                        .uri(spotify.next())
                        .retrieve()
                        .bodyToMono(SpotifyTrackResponse.class)
                        .block();

                tracks.addAll(spotify.items().stream().map(Item::track).toList());

                if (spotify.next() == null) {
                    hasNext = false;
                }
            }
        }


        return "user has " + tracks.size() + " tracks";
    }
}
