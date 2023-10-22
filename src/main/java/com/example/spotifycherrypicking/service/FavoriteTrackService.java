package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.UserProfileDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FavoriteTrackService {
    private final SpotifyWebService spotifyWebService;

    public FavoriteTrackService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public SequencedMap<String, List<Track>> fetchAndOrganizePlaylistsByArtistAndTrackCount() {
        Map<String, List<Track>> collect = spotifyWebService.fetchPlaylists()
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

    public UserProfileDto fetchMe() {
        return spotifyWebService.fetchUserProfile();
    }
}
