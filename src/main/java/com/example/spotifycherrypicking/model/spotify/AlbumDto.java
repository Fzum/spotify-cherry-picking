package com.example.spotifycherrypicking.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
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
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("release_date_precision") String releaseDatePrecision,
        String type,
        String uri,
        List<ArtistDto> artist
) {
    public LocalDate getNormalizedReleaseDate() {
        String normalizedReleaseDate = this.releaseDate;
        switch (this.releaseDatePrecision) {
            case "day":
                break;
            case "month":
                normalizedReleaseDate += "-01";  // add a default day
                break;
            case "year":
                normalizedReleaseDate += "-01-01";  // add a default month and day
                break;
            default:
                throw new IllegalArgumentException("Invalid precision: " + this.releaseDatePrecision);
        }

        return tryParse(normalizedReleaseDate, name);
    }

    private static LocalDate tryParse(String normalizedReleaseDate, String name) {
        try {
            return LocalDate.parse(normalizedReleaseDate);
        } catch (Exception e) {
            System.out.printf("invalid date for album %s. ignoring and returning default date%n", name);
            return LocalDate.parse("1900-01-01");
        }
    }
}
