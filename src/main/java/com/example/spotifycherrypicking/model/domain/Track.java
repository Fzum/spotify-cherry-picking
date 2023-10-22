package com.example.spotifycherrypicking.model.domain;

import java.time.LocalDate;

public record Track(
        String spotifyId,
        String name,
        String album,
        String uri,
        String artist,
        LocalDate albumReleaseDate
) {
}
