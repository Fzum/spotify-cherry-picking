package com.example.spotifycherrypicking.model;

import java.util.List;

public record SpotifyTrackResponse(
        int limit,
        String next,
        int offset,
        String previous,
        int total,
        List<Item> items
) {
}
