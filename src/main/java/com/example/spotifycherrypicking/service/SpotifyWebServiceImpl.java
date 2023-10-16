package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.ItemDto;
import com.example.spotifycherrypicking.model.spotify.SpotifyTrackResponseDto;
import com.example.spotifycherrypicking.model.spotify.TrackDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class SpotifyWebServiceImpl implements SpotifyWebService {
    private final WebClient spofifyWebClient;

    public SpotifyWebServiceImpl(WebClient spofifyWebClient) {
        this.spofifyWebClient = spofifyWebClient;
    }

    @Override
    public Stream<Track> fetchPlaylists() {
        boolean hasNext = true;

        SpotifyTrackResponseDto spotify = spofifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/me/tracks")
                        .queryParam("limit", 50)
                        .queryParam("market", "AT")
                        .build())
                .retrieve()
                .bodyToMono(SpotifyTrackResponseDto.class)
                .block();

        final List<TrackDto> trackDtos = new ArrayList<>(spotify.items().stream().map(ItemDto::track).toList());

        if (spotify.next() != null) {
            while (hasNext) {
                spotify = spofifyWebClient
                        .get()
                        .uri(spotify.next())
                        .retrieve()
                        .bodyToMono(SpotifyTrackResponseDto.class)
                        .block();

                trackDtos.addAll(spotify.items().stream().map(ItemDto::track).toList());

                if (spotify.next() == null) {
                    hasNext = false;
                }
            }
        }

        return trackDtos.stream()
                .map(d -> new Track(d.id(), d.name(), d.album().name(), d.artists().getFirst().name()));
    }
}
