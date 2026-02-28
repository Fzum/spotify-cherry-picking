package com.example.spotifycherrypicking.cli;

import com.example.spotifycherrypicking.model.domain.LibraryStats;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.service.CherryPickArtistService;
import com.example.spotifycherrypicking.service.FavoriteTrackSpotifyService;
import com.example.spotifycherrypicking.service.LibraryStatsService;
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
    private final LibraryStatsService libraryStatsService;

    public SpotifyCliRunner(
            FavoriteTrackSpotifyService favoriteTrackSpotifyService,
            MeSpotifyService meSpotifyService,
            CherryPickArtistService cherryPickArtistService,
            LibraryStatsService libraryStatsService) {
        this.favoriteTrackSpotifyService = favoriteTrackSpotifyService;
        this.meSpotifyService = meSpotifyService;
        this.cherryPickArtistService = cherryPickArtistService;
        this.libraryStatsService = libraryStatsService;
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
                    case "1" -> showMyProfile();
                    case "2" -> showLikedTracksByArtist();
                    case "3" -> rebuildCherryPickedPlaylists();
                    case "4" -> deleteAllCherryPickedPlaylists();
                    case "5" -> listAllCherryPickedPlaylists();
                    case "6" -> showLibraryStats();
                    case "7" -> rebuildDecadePlaylists();
                    case "8" -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please enter a number from 1 to 8.");
                }
                if (running) {
                    System.out.println();
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("Select an action:");
        System.out.println("  1) View my Spotify profile");
        System.out.println("  2) View liked tracks by artist (artists with \u22655 songs)");
        System.out.println("  3) Rebuild cherry-picked playlists (delete all + create new)");
        System.out.println("  4) Delete all cherry-picked playlists");
        System.out.println("  5) List all cherry-picked playlists (A-Z)");
        System.out.println("  6) Show library statistics");
        System.out.println("  7) Rebuild decade playlists (Decade Mix: 1990s …)");
        System.out.println("  8) Exit");
        System.out.print("> ");
    }

    private void showMyProfile() {
        System.out.println("Fetching your Spotify profile...");
        var profile = meSpotifyService.fetchMe();
        System.out.println("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        System.out.printf("  Display name : %s%n", profile.displayName());
        System.out.printf("  User ID      : %s%n", profile.id());
        System.out.println("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
    }

    private void showLikedTracksByArtist() {
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

    private void rebuildCherryPickedPlaylists() {
        System.out.println("Rebuilding cherry-picked playlists on Spotify...");
        System.out.println("(This may take a moment for large libraries.)");
        cherryPickArtistService.createCherryPickedPlaylists();
        System.out.println("Done! Cherry-picked playlists were rebuilt.");
    }

    private void deleteAllCherryPickedPlaylists() {
        System.out.println("Deleting all cherry-picked playlists...");
        cherryPickArtistService.deleteCherryPickedPlaylists();
        System.out.println("Done.");
    }

    private void listAllCherryPickedPlaylists() {
        System.out.println("Loading cherry-picked playlists...");
        var playlistNames = cherryPickArtistService.listCherryPickedPlaylistsAlphabetically();
        if (playlistNames.isEmpty()) {
            System.out.println("No cherry-picked playlists found.");
            return;
        }

        System.out.printf("Found %d cherry-picked playlist(s):%n", playlistNames.size());
        playlistNames.forEach(name -> System.out.printf("  - %s%n", name));
    }

    private void showLibraryStats() {
        System.out.println("Fetching your library statistics...");
        LibraryStats stats = libraryStatsService.fetchStats();
        System.out.println("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        System.out.printf("  Total liked tracks     : %d%n", stats.totalTracks());
        System.out.printf("  Unique artists         : %d%n", stats.totalArtists());
        System.out.printf("  Artists with \u22655 tracks : %d%n", stats.qualifyingArtistsCount());
        System.out.printf("  Top %d artists:%n", LibraryStatsService.TOP_ARTISTS_COUNT);
        for (int i = 0; i < stats.topArtists().size(); i++) {
            System.out.printf("    %d. %s%n", i + 1, stats.topArtists().get(i));
        }
        System.out.printf("  Tracks by decade:%n");
        stats.tracksByDecade().forEach((decade, count) ->
                System.out.printf("    %s : %d track(s)%n", decade, count));
        System.out.println("\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
    }

    private void rebuildDecadePlaylists() {
        System.out.println("Rebuilding decade playlists on Spotify...");
        System.out.println("(This may take a moment for large libraries.)");
        cherryPickArtistService.createDecadePlaylists();
        System.out.println("Done! Decade playlists were rebuilt.");
    }
}
