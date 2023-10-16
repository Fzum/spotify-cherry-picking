package com.example.spotifycherrypicking;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {
    private final OAuth2AuthorizedClientService authorizedClientService;

    SpotifyService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public String fetchPlaylists() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var oauthToken = (OAuth2AuthenticationToken) authentication;
        var oauth2Client = this.authorizedClientService
                .loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName());
        String jwt = oauth2Client.getAccessToken().getTokenValue();
        System.out.println(authentication);
        return authentication.getName();
    }
}
