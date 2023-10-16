package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.Item;
import com.example.spotifycherrypicking.model.SpotifyTrackResponse;
import com.example.spotifycherrypicking.model.Track;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteTrackService {
    private final WebClient spofifyWebClient;

    FavoriteTrackService(WebClient spofifyWebClient) {
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


        String trackNamesDelimited = tracks.stream().map(Track::name).collect(Collectors.joining("\n"));
        return "user has %d tracks: \n %s"
                .formatted(tracks.size(), trackNamesDelimited);
    }
}
