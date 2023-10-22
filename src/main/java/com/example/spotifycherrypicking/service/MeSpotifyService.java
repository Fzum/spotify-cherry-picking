package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.spotify.UserProfileDto;
import org.springframework.stereotype.Service;

@Service
public class MeSpotifyService {
    private final SpotifyWebService spotifyWebService;

    public MeSpotifyService(SpotifyWebService spotifyWebService) {
        this.spotifyWebService = spotifyWebService;
    }

    public UserProfileDto fetchMe() {
        return spotifyWebService.fetchUserProfile();
    }
}
