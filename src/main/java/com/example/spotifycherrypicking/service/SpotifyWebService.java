package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.CreatePlaylistRequestDto;
import com.example.spotifycherrypicking.model.spotify.PlaylistDto;
import com.example.spotifycherrypicking.model.spotify.UserProfileDto;

import java.util.Optional;
import java.util.stream.Stream;

interface SpotifyWebService {
    Stream<Track> fetchTracks();

    UserProfileDto fetchUserProfile();

    Stream<PlaylistDto> findPlaylistsByPrefix(String userId, String playlistPrefix);

    Optional<String> findPlaylistIdByName(String userId, String playlistName);

    String createPlaylist(String userId, CreatePlaylistRequestDto createPlaylistRequestDto);

    void addTracksToPlaylist(String playlistId, AddTracksToPlaylistDto addTracksToPlaylistDto);

    void replaceTracksInPlaylist(String playlistId, AddTracksToPlaylistDto addTracksToPlaylistDto);

    void deletePlaylist(String playlistId);
}
