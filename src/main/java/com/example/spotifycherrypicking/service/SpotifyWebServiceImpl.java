package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.ItemDto;
import com.example.spotifycherrypicking.model.spotify.SpotifyTrackResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
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

        final List<ItemDto> trackDtos = new ArrayList<>(spotify.items());

        if (spotify.next() != null) {
            while (hasNext) {
                spotify = spofifyWebClient
                        .get()
                        .uri(spotify.next())
                        .retrieve()
                        .bodyToMono(SpotifyTrackResponseDto.class)
                        .block();

                trackDtos.addAll(spotify.items());

                if (spotify.next() == null) {
                    hasNext = false;
                }
            }
        }

        return trackDtos.stream()
                .map(d -> new Track(
                        d.track().id(),
                        d.track().name(),
                        d.track().album().name(),
                        d.track().artists().getFirst().name(),
                        OffsetDateTime.parse(d.addedAt()).toLocalDateTime()
                ));
    }
}
