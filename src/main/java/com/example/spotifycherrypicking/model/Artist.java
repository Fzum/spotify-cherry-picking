package com.example.spotifycherrypicking.model;

import java.util.Map;

public record Artist(
        Map<String, String> externalUrls,
        String href,
        String id,
        String name,
        String type,
        String uri
) {}

