package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.spotify.CreatePlaylistRequestDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreatePlaylisSpotifytService {
    private final SpotifyWebService spotifyWebService;

    public CreatePlaylisSpotifytService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public String createPlaylists(String userId, CreatePlaylistRequestDto createPlaylistRequestDto) {
        return spotifyWebService.createPlaylist(userId, createPlaylistRequestDto);
    }

    public Optional<String> findPlaylistIdByName(String userId, String playlistName) {
        return spotifyWebService.findPlaylistIdByName(userId, playlistName);
    }
}
