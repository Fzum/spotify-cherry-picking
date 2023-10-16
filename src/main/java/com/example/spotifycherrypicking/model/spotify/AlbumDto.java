package com.example.spotifycherrypicking.model.spotify;

import java.util.List;
import java.util.Map;

public record AlbumDto(
        String albumType,
        int totalTracks,
        List<String> availableMarkets,
        Map<String, String> externalUrls,
        String href,
        String id,
        List<ImageDto> images,
        String name,
        String releaseDate,
        String releaseDatePrecision,
        String type,
        String uri,
        List<ArtistDto> artist
) {
}
