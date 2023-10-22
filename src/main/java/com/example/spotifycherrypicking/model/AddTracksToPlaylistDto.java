package com.example.spotifycherrypicking.model;

import java.util.SequencedSet;

public record AddTracksToPlaylistDto(SequencedSet<String> trackUris) {
}
