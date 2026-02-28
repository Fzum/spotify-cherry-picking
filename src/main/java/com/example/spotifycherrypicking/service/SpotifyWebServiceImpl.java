package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class SpotifyWebServiceImpl implements SpotifyWebService {
    private final WebClient spofifyWebClient;

    public SpotifyWebServiceImpl(WebClient spofifyWebClient) {
        this.spofifyWebClient = spofifyWebClient;
    }

    @Override
    public Stream<Track> fetchTracks() {
        boolean hasNext = true;

        SpotifyTrackResponseDto spotify = spofifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/me/tracks")
                        .queryParam("limit", 50)
                        .queryParam("market", "AT")
                        .build())
                .retrieve()
                .bodyToMono(SpotifyTrackResponseDto.class)
                .block();

        final List<ItemDto> trackDtos = new ArrayList<>(spotify.items());

        if (spotify.next() != null) {
            while (hasNext) {
                spotify = spofifyWebClient
                        .get()
                        .uri(spotify.next())
                        .retrieve()
                        .bodyToMono(SpotifyTrackResponseDto.class)
                        .block();

                trackDtos.addAll(spotify.items());

                if (spotify.next() == null) {
                    hasNext = false;
                }
            }
        }

        return trackDtos.stream()
                .map(d -> {
                    var track = d.track();
                    return new Track(
                            track.id(),
                            track.name(),
                            track.album().name(),
                            track.uri(),
                            track.artists().getFirst().name(),
                            track.album().getNormalizedReleaseDate()
                    );
                });
    }

    @Override
    public UserProfileDto fetchUserProfile() {
        return spofifyWebClient
                .get()
                .uri("/v1/me")
                .retrieve()
                .bodyToMono(UserProfileDto.class)
                .block();
    }

    @Override
    public Stream<PlaylistDto> findPlaylistsByPrefix(String userId, String playlistPrefix) {
        List<PlaylistDto> playlistsByPrefix = new ArrayList<>();
        PlaylistPageDto playlistPage = spofifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/me/playlists")
                        .queryParam("limit", 50)
                        .build())
                .retrieve()
                .bodyToMono(PlaylistPageDto.class)
                .block();

        while (playlistPage != null) {
            var playlists = playlistPage.items() == null ? Collections.<PlaylistDto>emptyList() : playlistPage.items();
            playlists.stream()
                    .filter(playlist -> playlist.name() != null
                            && playlist.name().toLowerCase().startsWith(playlistPrefix.toLowerCase()))
                    .filter(playlist -> playlist.owner() != null
                            && playlist.owner().id() != null
                            && userId.equalsIgnoreCase(playlist.owner().id()))
                    .forEach(playlistsByPrefix::add);

            if (playlistPage.next() == null) {
                break;
            }

            playlistPage = spofifyWebClient
                    .get()
                    .uri(playlistPage.next())
                    .retrieve()
                    .bodyToMono(PlaylistPageDto.class)
                    .block();
        }

        return playlistsByPrefix.stream();
    }

    @Override
    public Optional<String> findPlaylistIdByName(String userId, String playlistName) {
        PlaylistPageDto playlistPage = spofifyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/me/playlists")
                        .queryParam("limit", 50)
                        .build())
                .retrieve()
                .bodyToMono(PlaylistPageDto.class)
                .block();

        while (playlistPage != null) {
            var playlists = playlistPage.items() == null ? Collections.<PlaylistDto>emptyList() : playlistPage.items();
            var found = playlists.stream()
                    .filter(playlist -> playlistName.equals(playlist.name()))
                    .filter(playlist -> playlist.owner() != null && userId.equals(playlist.owner().id()))
                    .map(PlaylistDto::id)
                    .findFirst();

            if (found.isPresent()) {
                return found;
            }

            if (playlistPage.next() == null) {
                return Optional.empty();
            }

            playlistPage = spofifyWebClient
                    .get()
                    .uri(playlistPage.next())
                    .retrieve()
                    .bodyToMono(PlaylistPageDto.class)
                    .block();
        }

        return Optional.empty();
    }

    @Override
    public String createPlaylist(String userId, CreatePlaylistRequestDto createPlaylistRequestDto) {
        return spofifyWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/users/{user_id}/playlists")
                        .build(userId))
                .bodyValue(createPlaylistRequestDto)
                .retrieve()
                .bodyToMono(CreatePlaylistResponseDto.class)
                .block()
                .id();
    }

    @Override
    public void addTracksToPlaylist(String playlistId, AddTracksToPlaylistDto addTracksToPlaylistDto) {
        spofifyWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/playlists/{playlist_id}/tracks")
                        .build(playlistId))
                .bodyValue(addTracksToPlaylistDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.out.println(e.getMessage()))
                .block();
    }

    @Override
    public void replaceTracksInPlaylist(String playlistId, AddTracksToPlaylistDto addTracksToPlaylistDto) {
        spofifyWebClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/playlists/{playlist_id}/tracks")
                        .build(playlistId))
                .bodyValue(addTracksToPlaylistDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.out.println(e.getMessage()))
                .block();
    }

    @Override
    public void deletePlaylist(String playlistId) {
        spofifyWebClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/playlists/{playlist_id}/followers")
                        .build(playlistId))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.out.println(e.getMessage()))
                .block();
    }
}
