package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.CreatePlaylistRequestDto;
import com.example.spotifycherrypicking.model.spotify.PlaylistDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CherryPickArtistService {
    private static final String CHERRY_PICKED_PLAYLIST_PREFIX = "Cherry Picked: ";
    private static final String DECADE_PLAYLIST_PREFIX = "Decade Mix: ";

    private final FavoriteTrackSpotifyService favoriteTrackSpotifyService;
    private final CreatePlaylisSpotifytService createPlaylisSpotifytService;
    private final MeSpotifyService meSpotifyService;
    private final AddTracksToPlaylistSpotifyService addTracksToPlaylistSpotifyService;
    private final SpotifyWebService spotifyWebService;

    public CherryPickArtistService(FavoriteTrackSpotifyService favoriteTrackSpotifyService,
                                   CreatePlaylisSpotifytService createPlaylisSpotifytService,
                                   MeSpotifyService meSpotifyService,
                                   AddTracksToPlaylistSpotifyService addTracksToPlaylistSpotifyService,
                                   SpotifyWebService spotifyWebService) {
        this.favoriteTrackSpotifyService = favoriteTrackSpotifyService;
        this.createPlaylisSpotifytService = createPlaylisSpotifytService;
        this.meSpotifyService = meSpotifyService;
        this.addTracksToPlaylistSpotifyService = addTracksToPlaylistSpotifyService;
        this.spotifyWebService = spotifyWebService;
    }

    public void createCherryPickedPlaylists() {
        var myFavoriteTracksByArtistAndTrackCountMap = favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount();
        var me = meSpotifyService.fetchMe();

        deleteCherryPickedPlaylists();

        myFavoriteTracksByArtistAndTrackCountMap.forEach((artist, tracks) -> {
            var playlistName = String.format("Cherry Picked: %s", artist);
            var createPlaylistRequestDto = new CreatePlaylistRequestDto(playlistName, getDescription(artist), false);
            System.out.printf("Creating playlist: %s%n", playlistName);
            var playlistId = createPlaylisSpotifytService.createPlaylists(me.id(), createPlaylistRequestDto);
            var trackUris = tracks.stream().map(Track::uri).collect(Collectors.toCollection(LinkedHashSet::new));

            System.out.printf("Adding %d track(s) to playlist: %s%n", trackUris.size(), playlistName);
            addTracksToPlaylistSpotifyService.add(playlistId, new AddTracksToPlaylistDto(trackUris));
        });
    }

    public void deleteCherryPickedPlaylists() {
        var me = meSpotifyService.fetchMe();
        var playlistsToDelete = spotifyWebService.findPlaylistsByPrefix(me.id(), CHERRY_PICKED_PLAYLIST_PREFIX).toList();

        if (playlistsToDelete.isEmpty()) {
            System.out.println("No cherry-picked playlists found to delete.");
            return;
        }

        System.out.printf("Deleting %d cherry-picked playlist(s):%n", playlistsToDelete.size());
        playlistsToDelete.forEach(playlist -> {
            System.out.printf("  - Deleting playlist: %s%n", playlist.name());
            spotifyWebService.deletePlaylist(playlist.id());
        });
    }

    public List<String> listCherryPickedPlaylistsAlphabetically() {
        var me = meSpotifyService.fetchMe();
        return spotifyWebService.findPlaylistsByPrefix(me.id(), CHERRY_PICKED_PLAYLIST_PREFIX)
                .map(playlist -> playlist.name())
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    private static String getDescription(String artist) {
        return String.format("Cherry picked tracks from %s", artist);
    }

    public void createDecadePlaylists() {
        var tracksByDecade = favoriteTrackSpotifyService.groupTracksByDecade();
        var me = meSpotifyService.fetchMe();

        deleteDecadePlaylists();

        tracksByDecade.forEach((decade, tracks) -> {
            var playlistName = DECADE_PLAYLIST_PREFIX + decade;
            var description = String.format("Cherry picked tracks from the %s", decade);
            var createDto = new CreatePlaylistRequestDto(playlistName, description, false);
            System.out.printf("Creating decade playlist: %s%n", playlistName);
            var playlistId = createPlaylisSpotifytService.createPlaylists(me.id(), createDto);
            var trackUris = tracks.stream().map(Track::uri).collect(Collectors.toCollection(LinkedHashSet::new));
            System.out.printf("Adding %d track(s) to: %s%n", trackUris.size(), playlistName);
            addTracksToPlaylistSpotifyService.add(playlistId, new AddTracksToPlaylistDto(trackUris));
        });
    }

    public void deleteDecadePlaylists() {
        var me = meSpotifyService.fetchMe();
        var toDelete = spotifyWebService.findPlaylistsByPrefix(me.id(), DECADE_PLAYLIST_PREFIX).toList();

        if (toDelete.isEmpty()) {
            System.out.println("No decade playlists found to delete.");
            return;
        }

        System.out.printf("Deleting %d decade playlist(s):%n", toDelete.size());
        toDelete.forEach(playlist -> {
            System.out.printf("  - Deleting: %s%n", playlist.name());
            spotifyWebService.deletePlaylist(playlist.id());
        });
    }

    public List<String> listDecadePlaylists() {
        var me = meSpotifyService.fetchMe();
        return spotifyWebService.findPlaylistsByPrefix(me.id(), DECADE_PLAYLIST_PREFIX)
                .map(PlaylistDto::name)
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
