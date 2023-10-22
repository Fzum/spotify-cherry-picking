package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.CreatePlaylistRequestDto;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
public class CherryPickArtistService {
    private final FavoriteTrackSpotifyService favoriteTrackSpotifyService;
    private final CreatePlaylisSpotifytService createPlaylisSpotifytService;
    private final MeSpotifyService meSpotifyService;
    private final AddTracksToPlaylistSpotifyService addTracksToPlaylistSpotifyService;

    public CherryPickArtistService(FavoriteTrackSpotifyService favoriteTrackSpotifyService,
                                   CreatePlaylisSpotifytService createPlaylisSpotifytService,
                                   MeSpotifyService meSpotifyService, AddTracksToPlaylistSpotifyService addTracksToPlaylistSpotifyService) {
        this.favoriteTrackSpotifyService = favoriteTrackSpotifyService;
        this.createPlaylisSpotifytService = createPlaylisSpotifytService;
        this.meSpotifyService = meSpotifyService;
        this.addTracksToPlaylistSpotifyService = addTracksToPlaylistSpotifyService;
    }

    public void createCherryPickedPlaylists() {
        var myFavoriteTracksByArtistAndTrackCountMap = favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount();
        var me = meSpotifyService.fetchMe();

        myFavoriteTracksByArtistAndTrackCountMap.forEach((artist, tracks) -> {
            var playlistName = String.format("Cherry Picked: %s", artist);
            var createPlaylistRequestDto = new CreatePlaylistRequestDto(playlistName, getDescription(artist), false);
            var playlistId = createPlaylisSpotifytService.createPlaylists(me.id(), createPlaylistRequestDto);
            var trackUris = tracks.stream().map(Track::uri).collect(Collectors.toCollection(LinkedHashSet::new));

            addTracksToPlaylistSpotifyService.add(playlistId, new AddTracksToPlaylistDto(trackUris));
        });
    }

    private static String getDescription(String artist) {
        return String.format("Cherry picked tracks from %s", artist);
    }
}
