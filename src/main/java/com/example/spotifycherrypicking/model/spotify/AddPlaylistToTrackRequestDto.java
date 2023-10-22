package com.example.spotifycherrypicking.model.spotify;

import java.util.Set;

public record AddPlaylistToTrackRequestDto(
        Set<String> trackUris
) {
}
