package com.example.spotifycherrypicking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.SequencedSet;

public record AddTracksToPlaylistDto(@JsonProperty("uris") SequencedSet<String> trackUris) {
}
