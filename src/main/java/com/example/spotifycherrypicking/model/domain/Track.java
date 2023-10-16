package com.example.spotifycherrypicking.model.domain;

import java.time.LocalDateTime;

public record Track(
        String spotifyId,
        String name,
        String album,
        String artist,
        LocalDateTime addedAt
) {
}
