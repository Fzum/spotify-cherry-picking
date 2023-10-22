package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.TrackDto;
import com.example.spotifycherrypicking.model.spotify.UserProfileDto;

import java.util.stream.Stream;

interface SpotifyWebService {
    Stream<Track> fetchPlaylists();

    UserProfileDto fetchUserProfile();
}
