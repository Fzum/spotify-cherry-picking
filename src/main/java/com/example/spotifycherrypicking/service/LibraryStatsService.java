package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.LibraryStats;
import com.example.spotifycherrypicking.model.domain.Track;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class LibraryStatsService {

    static final int QUALIFYING_ARTIST_MIN_TRACKS = 5;
    public static final int TOP_ARTISTS_COUNT = 5;

    private final SpotifyWebService spotifyWebService;

    public LibraryStatsService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public LibraryStats fetchStats() {
        List<Track> allTracks = spotifyWebService.fetchTracks().toList();

        Map<String, Long> countByArtist = allTracks.stream()
                .collect(Collectors.groupingBy(Track::artist, Collectors.counting()));

        long qualifyingArtists = countByArtist.values().stream()
                .filter(count -> count >= QUALIFYING_ARTIST_MIN_TRACKS)
                .count();

        List<String> topArtists = countByArtist.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TOP_ARTISTS_COUNT)
                .map(Map.Entry::getKey)
                .toList();

        Map<String, Long> tracksByDecade = new TreeMap<>(
                allTracks.stream()
                        .collect(Collectors.groupingBy(
                                track -> toDecadeLabel(track.albumReleaseDate().getYear()),
                                Collectors.counting()
                        ))
        );

        return new LibraryStats(
                allTracks.size(),
                countByArtist.size(),
                (int) qualifyingArtists,
                topArtists,
                tracksByDecade
        );
    }

    static String toDecadeLabel(int year) {
        return ((year / 10) * 10) + "s";
    }
}
