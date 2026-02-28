package com.example.spotifycherrypicking.controller;

import com.example.spotifycherrypicking.service.CherryPickArtistService;
import com.example.spotifycherrypicking.service.FavoriteTrackSpotifyService;
import com.example.spotifycherrypicking.service.LibraryStatsService;
import com.example.spotifycherrypicking.service.MeSpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpotifyController {

    private final FavoriteTrackSpotifyService favoriteTrackSpotifyService;
    private final MeSpotifyService meSpotifyService;
    private final CherryPickArtistService cherryPickArtistService;
    private final LibraryStatsService libraryStatsService;

    public SpotifyController(
            FavoriteTrackSpotifyService favoriteTrackSpotifyService,
            MeSpotifyService meSpotifyService,
            CherryPickArtistService cherryPickArtistService,
            LibraryStatsService libraryStatsService) {
        this.favoriteTrackSpotifyService = favoriteTrackSpotifyService;
        this.meSpotifyService = meSpotifyService;
        this.cherryPickArtistService = cherryPickArtistService;
        this.libraryStatsService = libraryStatsService;
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
        return "Playlists synced (created or updated)!";
    }

    @GetMapping("/stats")
    public String getStats() {
        return libraryStatsService.fetchStats().toString();
    }

    @GetMapping("/create-decade-playlists")
    public String createDecadePlaylists() {
        cherryPickArtistService.createDecadePlaylists();
        return "Decade playlists synced (created or updated)!";
    }

    public record Test(String test) {
    }

    @GetMapping("/test")
    public ResponseEntity<Test> test() {
        return ResponseEntity.ok(new Test("test"));
    }
}
