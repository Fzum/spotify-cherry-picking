package com.example.spotifycherrypicking.model;

import java.util.List;
import java.util.Map;

public record Track(
        Album album,
        List<Artist> artists,
        List<String> availableMarkets,
        int discNumber,
        long durationMs,
        boolean explicit,
        Map<String, String> externalIds,
        Map<String, String> externalUrls,
        String href,
        String id,
        String name,
        int popularity,
        String previewUrl,
        int trackNumber,
        String type,
        String uri,
        boolean isLocal
) {
}
