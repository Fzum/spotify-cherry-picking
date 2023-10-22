package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteTrackServiceTest {

    @Mock
    private SpotifyWebService spotifyWebService;

    @InjectMocks
    private FavoriteTrackService favoriteTrackService;

    @Test
    @DisplayName("should return map of tracks to artist when artist appears more or five times")
    void shouldReturnMapOfTracksToArtist() {
        // given
        var currentDateTime = LocalDateTime.now();

        var random = new Random();

        when(spotifyWebService.fetchPlaylists())
                .thenReturn(Stream.of(
                        new Track("1", "1", "hwh1", "westside gunn", currentDateTime),
                        new Track("2", "2", "hwh2", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("3", "3", "hwh2", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("4", "1", "la machina", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("5", "2", "la machina", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("6", "1", "plugs i met", "benny the butcher", currentDateTime.plusDays(random.nextInt(30))),
                        // Adding more tracks with different LocalDateTime values
                        new Track("7", "4", "pray for paris", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("8", "5", "burden of proof", "benny the butcher", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("9", "6", "from king to a god", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("10", "7", "who made the sunshine", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("11", "8", "hwh3", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("12", "9", "hwh3", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("13", "10", "la machina", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("14", "11", "plugs i met 2", "benny the butcher", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("15", "12", "god don’t make mistakes", "conway the machine", currentDateTime.plusDays(random.nextInt(30)))
                ));

        // when
        var artistSongMap = favoriteTrackService.fetchAndOrganizePlaylistsByArtistAndTrackCount();

        // then
        assertThat(artistSongMap.pollFirstEntry()).satisfies(entry -> {
            assertThat(entry.getKey()).isEqualTo("westside gunn");
            assertThat(entry.getValue()).hasSize(7);
        });

        assertThat(artistSongMap.pollFirstEntry()).satisfies(entry -> {
            assertThat(entry.getKey()).isEqualTo("conway the machine");
            assertThat(entry.getValue()).hasSize(5);
        });

        assertThat(artistSongMap).isEmpty();
    }

}