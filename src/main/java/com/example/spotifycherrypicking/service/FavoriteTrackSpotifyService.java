package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FavoriteTrackSpotifyService {
    private final SpotifyWebService spotifyWebService;

    public FavoriteTrackSpotifyService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public SequencedMap<String, List<Track>> fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount() {
        Map<String, List<Track>> collect = spotifyWebService.fetchTracks()
                .collect(Collectors.groupingBy(
                        Track::artist,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Function.identity(), Collectors.toList()),
                                perArtistTracks -> {
                                    perArtistTracks.sort(Comparator.comparing(Track::addedAt));
                                    return perArtistTracks.reversed();
                                }
                        )
                ));

        LinkedHashMap<String, List<Track>> organizedSongsMap = collect.entrySet()
                .stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .filter(e -> e.getValue().size() >= 5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return organizedSongsMap;
    }
}
