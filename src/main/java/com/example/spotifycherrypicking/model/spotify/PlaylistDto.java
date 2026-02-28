package com.example.spotifycherrypicking.model.spotify;

public record PlaylistDto(
        String id,
        String name,
        PlaylistOwnerDto owner
) {
}
