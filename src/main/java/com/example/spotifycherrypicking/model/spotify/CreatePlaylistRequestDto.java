package com.example.spotifycherrypicking.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePlaylistRequestDto(
        String name,
        String description,
        @JsonProperty("public") boolean isPublic
) {
}
