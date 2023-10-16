package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class FavoriteTrackService {
    private final SpotifyWebService spotifyWebService;

    public FavoriteTrackService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public String fetchPlaylists() {
        Stream<Track> trackStream = spotifyWebService.fetchPlaylists();

        return "success";
    }
}
