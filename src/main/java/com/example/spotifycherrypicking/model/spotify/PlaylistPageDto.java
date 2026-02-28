package com.example.spotifycherrypicking.model.spotify;

import java.util.List;

public record PlaylistPageDto(
        List<PlaylistDto> items,
        String next
) {
}
