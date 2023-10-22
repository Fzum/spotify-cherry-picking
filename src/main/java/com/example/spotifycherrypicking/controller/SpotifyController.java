package com.example.spotifycherrypicking.controller;

import com.example.spotifycherrypicking.service.FavoriteTrackService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotifyController {

    private final FavoriteTrackService spotifyService;

    public SpotifyController(FavoriteTrackService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/playlists")
    public String getPlaylists() {
        return spotifyService.fetchAndOrganizePlaylistsByArtistAndTrackCount().toString();
    }

    @GetMapping("/me")
    public String getMe() {
        return spotifyService.fetchMe().toString();
    }
}
