package com.example.spotifycherrypicking.model.domain;

public record Track(
        String spotifyId,
        String name,
        String album,
        String artist
) {
}
