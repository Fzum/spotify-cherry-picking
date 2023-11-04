package com.example.spotifycherrypicking.controller;

import com.example.spotifycherrypicking.service.CherryPickArtistService;
import com.example.spotifycherrypicking.service.FavoriteTrackSpotifyService;
import com.example.spotifycherrypicking.service.MeSpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotifyController {

    private final FavoriteTrackSpotifyService favoriteTrackSpotifyService;
    private final MeSpotifyService meSpotifyService;
    private final CherryPickArtistService cherryPickArtistService;

    public SpotifyController(
            FavoriteTrackSpotifyService favoriteTrackSpotifyService,
            MeSpotifyService meSpotifyService, CherryPickArtistService cherryPickArtistService) {
        this.favoriteTrackSpotifyService = favoriteTrackSpotifyService;
        this.meSpotifyService = meSpotifyService;
        this.cherryPickArtistService = cherryPickArtistService;
    }

    @GetMapping("/playlists")
    public String getPlaylists() {
        return favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount().toString();
    }

    @GetMapping("/home")
    public String getMe() {
        return meSpotifyService.fetchMe().toString();
    }

    @GetMapping("/create-cherry-picked-playlists")
    public String createPlaylists() {
        cherryPickArtistService.createCherryPickedPlaylists();
        return "Playlists created!";
    }

    public record Test(String test) {
    }

    @GetMapping("/test")
    public ResponseEntity<Test> test() {
        return ResponseEntity.ok(new Test("test"));
    }
}
