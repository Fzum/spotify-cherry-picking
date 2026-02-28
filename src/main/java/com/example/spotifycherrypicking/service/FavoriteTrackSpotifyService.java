package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.spotifycherrypicking.service.LibraryStatsService.toDecadeLabel;

@Service
public class FavoriteTrackSpotifyService {
    static final int MIN_TRACKS_PER_DECADE = 3;

    private final SpotifyWebService spotifyWebService;

    public FavoriteTrackSpotifyService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public SequencedMap<String, List<Track>> fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount() {
        Map<String, List<Track>> collect = spotifyWebService.fetchTracks()
                .collect(Collectors.groupingBy(
                        Track::artist,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                perArtistTracks -> {
                                    perArtistTracks.sort(Comparator.comparing(Track::albumReleaseDate));
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

    /**
     * Groups all liked tracks by their release decade (e.g. "1990s"), sorted chronologically.
     * Only decades with at least {@value MIN_TRACKS_PER_DECADE} tracks are included.
     */
    public SequencedMap<String, List<Track>> groupTracksByDecade() {
        return spotifyWebService.fetchTracks()
                .collect(Collectors.groupingBy(
                        track -> toDecadeLabel(track.albumReleaseDate().getYear()),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                perDecadeTracks -> {
                                    perDecadeTracks.sort(Comparator.comparing(Track::albumReleaseDate).reversed());
                                    return perDecadeTracks;
                                }
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(e -> e.getValue().size() >= MIN_TRACKS_PER_DECADE)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
