package com.example.spotifycherrypicking.model.spotify;

public record CreatePlaylistRequestDto(
        String name,
        String description,
        boolean isPublic
) {
}
