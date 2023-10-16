package com.example.spotifycherrypicking.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemDto(
        @JsonProperty("added_at") String addedAt,
        TrackDto track
) {
}
