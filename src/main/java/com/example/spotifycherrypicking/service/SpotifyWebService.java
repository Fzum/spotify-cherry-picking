package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.CreatePlaylistRequestDto;
import com.example.spotifycherrypicking.model.spotify.UserProfileDto;

import java.util.stream.Stream;

interface SpotifyWebService {
    Stream<Track> fetchTracks();

    UserProfileDto fetchUserProfile();

    String createPlaylist(String userId, CreatePlaylistRequestDto createPlaylistRequestDto);

    void addTracksToPlaylist(String playlistId, AddTracksToPlaylistDto addTracksToPlaylistDto);
}
