package com.example.spotifycherrypicking.cli;

import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.service.CherryPickArtistService;
import com.example.spotifycherrypicking.service.FavoriteTrackSpotifyService;
import com.example.spotifycherrypicking.service.MeSpotifyService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
@Profile("cli")
public class SpotifyCliRunner implements ApplicationRunner {

    private final FavoriteTrackSpotifyService favoriteTrackSpotifyService;
    private final MeSpotifyService meSpotifyService;
    private final CherryPickArtistService cherryPickArtistService;

    public SpotifyCliRunner(
            FavoriteTrackSpotifyService favoriteTrackSpotifyService,
            MeSpotifyService meSpotifyService,
            CherryPickArtistService cherryPickArtistService) {
        this.favoriteTrackSpotifyService = favoriteTrackSpotifyService;
        this.meSpotifyService = meSpotifyService;
        this.cherryPickArtistService = cherryPickArtistService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Spotify Cherry Picking CLI         ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String input = scanner.nextLine().trim();
                System.out.println();
                switch (input) {
                    case "1" -> showProfile();
                    case "2" -> showTracksByArtist();
                    case "3" -> createCherryPickedPlaylists();
                    case "4" -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please enter 1, 2, 3, or 4.");
                }
                if (running) {
                    System.out.println();
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("What would you like to do?");
        System.out.println("  1) Show my Spotify profile");
        System.out.println("  2) Show liked tracks grouped by artist (artists with \u22655 liked songs)");
        System.out.println("  3) Create cherry-picked playlists on Spotify");
        System.out.println("  4) Exit");
        System.out.print("> ");
    }

    private void showProfile() {
        System.out.println("Fetching your Spotify profile...");
        var profile = meSpotifyService.fetchMe();
        System.out.println("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        System.out.printf("  Display name : %s%n", profile.displayName());
        System.out.printf("  User ID      : %s%n", profile.id());
        System.out.println("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
    }

    private void showTracksByArtist() {
        System.out.println("Fetching and organising your liked tracks...");
        Map<String, List<Track>> tracksByArtist =
                favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount();

        if (tracksByArtist.isEmpty()) {
            System.out.println("No artists found with 5 or more liked tracks.");
            return;
        }

        System.out.printf("%nFound %d artist(s) with 5 or more liked tracks:%n%n", tracksByArtist.size());
        tracksByArtist.forEach((artist, tracks) -> {
            System.out.printf("  %-40s  %d tracks%n", artist, tracks.size());
            tracks.forEach(t -> System.out.printf("      - %s  (%s)%n", t.name(), t.albumReleaseDate()));
        });
    }

    private void createCherryPickedPlaylists() {
        System.out.println("Creating cherry-picked playlists on Spotify...");
        System.out.println("(This may take a moment for large libraries.)");
        cherryPickArtistService.createCherryPickedPlaylists();
        System.out.println("Done! Check your Spotify account for the new playlists.");
    }
}
