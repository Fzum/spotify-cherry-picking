package com.example.spotifycherrypicking.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UserProfileDto(
        String country,
        @JsonProperty("display_name") String displayName,
        String email,
        ExplicitContent explicitContent,
        ExternalUrls externalUrls,
        Followers followers,
        String href,
        String id,
        List<Image> images,
        String product,
        String type,
        String uri
) {
}

record ExplicitContent(
        @JsonProperty("filter_enabled") boolean filterEnabled,
        @JsonProperty("filter_locked") boolean filterLocked
) {
}

record ExternalUrls(
        String spotify
) {
}

record Followers(
        String href,
        int total
) {
}

record Image(
        String url,
        int height,
        int width
) {
}
