package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.AddTracksToPlaylistDto;
import com.example.spotifycherrypicking.model.domain.Track;
import com.example.spotifycherrypicking.model.spotify.PlaylistDto;
import com.example.spotifycherrypicking.model.spotify.UserProfileDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CherryPickArtistServiceTest {

    @Mock
    private FavoriteTrackSpotifyService favoriteTrackSpotifyService;

    @Mock
    private CreatePlaylisSpotifytService createPlaylisSpotifytService;

    @Mock
    private MeSpotifyService meSpotifyService;

    @Mock
    private AddTracksToPlaylistSpotifyService addTracksToPlaylistSpotifyService;

    @Mock
    private SpotifyWebService spotifyWebService;

    @InjectMocks
    private CherryPickArtistService cherryPickArtistService;

    @Test
    @DisplayName("should delete existing cherry-picked playlists before creating new ones")
    void shouldDeleteExistingBeforeCreate() {
        // given
        var userId = "user-1";
        var newPlaylistId = "playlist-new-1";
        var artistTracks = List.of(
                new Track("1", "track-1", "album", "spotify:track:1", "westside gunn", LocalDate.now()),
                new Track("2", "track-2", "album", "spotify:track:2", "westside gunn", LocalDate.now())
        );
        var favoritesByArtist = new LinkedHashMap<String, List<Track>>();
        favoritesByArtist.put("westside gunn", artistTracks);

        when(favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount())
                .thenReturn(favoritesByArtist);
        when(meSpotifyService.fetchMe()).thenReturn(userProfile(userId));
        when(spotifyWebService.findPlaylistsByPrefix(userId, "Cherry Picked: "))
                .thenReturn(Stream.of(
                        new PlaylistDto("old-1", "Cherry Picked: old-a", null),
                        new PlaylistDto("old-2", "Cherry Picked: old-b", null)
                ));
        when(createPlaylisSpotifytService.createPlaylists(eq(userId), any())).thenReturn(newPlaylistId);

        // when
        cherryPickArtistService.createCherryPickedPlaylists();

        // then
        verify(spotifyWebService).deletePlaylist("old-1");
        verify(spotifyWebService).deletePlaylist("old-2");
        verify(createPlaylisSpotifytService).createPlaylists(eq(userId), any());

        var tracksCaptor = ArgumentCaptor.forClass(AddTracksToPlaylistDto.class);
        verify(addTracksToPlaylistSpotifyService).add(eq(newPlaylistId), tracksCaptor.capture());
        assertThat(tracksCaptor.getValue().trackUris())
                .containsExactly("spotify:track:1", "spotify:track:2");
    }

    @Test
    @DisplayName("should create cherry-picked playlist when none exist yet")
    void shouldCreatePlaylistWhenNoExistingCherryPickedPlaylists() {
        // given
        var userId = "user-2";
        var newPlaylistId = "playlist-2";
        var artistTracks = List.of(
                new Track("10", "track-10", "album", "spotify:track:10", "conway the machine", LocalDate.now()),
                new Track("11", "track-11", "album", "spotify:track:11", "conway the machine", LocalDate.now())
        );
        var favoritesByArtist = new LinkedHashMap<String, List<Track>>();
        favoritesByArtist.put("conway the machine", artistTracks);

        when(favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount())
                .thenReturn(favoritesByArtist);
        when(meSpotifyService.fetchMe()).thenReturn(userProfile(userId));
        when(spotifyWebService.findPlaylistsByPrefix(userId, "Cherry Picked: "))
                .thenReturn(Stream.empty());
        when(createPlaylisSpotifytService.createPlaylists(eq(userId), any())).thenReturn(newPlaylistId);

        // when
        cherryPickArtistService.createCherryPickedPlaylists();

        // then
        verify(createPlaylisSpotifytService).createPlaylists(eq(userId), any());

        var tracksCaptor = ArgumentCaptor.forClass(AddTracksToPlaylistDto.class);
        verify(addTracksToPlaylistSpotifyService).add(eq(newPlaylistId), tracksCaptor.capture());
        assertThat(tracksCaptor.getValue().trackUris())
                .containsExactly("spotify:track:10", "spotify:track:11");
    }

    @Test
    @DisplayName("should return cherry-picked playlist names in alphabetical order")
    void shouldListCherryPickedPlaylistsAlphabetically() {
        // given
        var userId = "user-3";
        when(meSpotifyService.fetchMe()).thenReturn(userProfile(userId));
        when(spotifyWebService.findPlaylistsByPrefix(userId, "Cherry Picked: "))
                .thenReturn(Stream.of(
                        new PlaylistDto("1", "Cherry Picked: Westside Gunn", null),
                        new PlaylistDto("2", "Cherry Picked: Benny the Butcher", null),
                        new PlaylistDto("3", "Cherry Picked: Conway the Machine", null)
                ));

        // when
        var playlistNames = cherryPickArtistService.listCherryPickedPlaylistsAlphabetically();

        // then
        assertThat(playlistNames).containsExactly(
                "Cherry Picked: Benny the Butcher",
                "Cherry Picked: Conway the Machine",
                "Cherry Picked: Westside Gunn"
        );
    }

    private static UserProfileDto userProfile(String userId) {
        return new UserProfileDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                userId,
                null,
                null,
                null,
                null
        );
    }
}
