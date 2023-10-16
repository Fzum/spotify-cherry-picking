package com.example.spotifycherrypicking.model;

import java.util.List;
import java.util.Map;

public record Album(
        String albumType,
        int totalTracks,
        List<String> availableMarkets,
        Map<String, String> externalUrls,
        String href,
        String id,
        List<Image> images,
        String name,
        String releaseDate,
        String releaseDatePrecision,
        String type,
        String uri,
        List<Artist> artists
) {
}
