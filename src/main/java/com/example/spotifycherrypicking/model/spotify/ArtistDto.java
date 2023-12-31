package com.example.spotifycherrypicking.model.spotify;

import java.util.Map;

public record ArtistDto(
        Map<String, String> externalUrls,
        String href,
        String id,
        String name,
        String type,
        String uri
) {}

