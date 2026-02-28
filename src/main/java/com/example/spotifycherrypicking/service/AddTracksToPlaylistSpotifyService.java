package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import org.springframework.stereotype.Service;

@Service
public class AddTracksToPlaylistSpotifyService {
    private final SpotifyWebService spotifyWebService;

    public AddTracksToPlaylistSpotifyService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public void add(String playlistId, AddTracksToPlaylistDto trackUris) {
        spotifyWebService.addTracksToPlaylist(playlistId, trackUris);
    }

    public void replace(String playlistId, AddTracksToPlaylistDto trackUris) {
        spotifyWebService.replaceTracksInPlaylist(playlistId, trackUris);
    }
}
