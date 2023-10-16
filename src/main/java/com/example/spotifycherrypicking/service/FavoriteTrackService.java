package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FavoriteTrackService {
    private final SpotifyWebService spotifyWebService;

    public FavoriteTrackService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public Map<String, List<Track>> fetchPlaylists() {
        Map<String, List<Track>> collect = spotifyWebService.fetchPlaylists()
                .collect(Collectors.groupingBy(
                        Track::artist,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Function.identity(), Collectors.toList()),
                                perArtistTracks -> {
                                    perArtistTracks.sort(Comparator.comparing(Track::addedAt));
                                    return perArtistTracks;
                                }
                        )
                ));
        return collect;
    }
}
