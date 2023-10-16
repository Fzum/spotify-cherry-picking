package com.example.spotifycherrypicking.model.spotify;

import java.util.List;

public record SpotifyTrackResponseDto(
        int limit,
        String next,
        int offset,
        String previous,
        int total,
        List<ItemDto> items
) {
}
