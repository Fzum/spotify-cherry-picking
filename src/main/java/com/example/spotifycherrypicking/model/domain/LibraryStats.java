package com.example.spotifycherrypicking.model.domain;

import java.util.List;
import java.util.Map;

public record LibraryStats(
        int totalTracks,
        int totalArtists,
        int qualifyingArtistsCount,
        List<String> topArtists,
        Map<String, Long> tracksByDecade) {
}
