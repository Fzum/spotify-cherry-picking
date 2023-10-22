package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.Track;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteTrackSpotifyServiceTest {

    @Mock
    private SpotifyWebService spotifyWebService;

    @InjectMocks
    private FavoriteTrackSpotifyService favoriteTrackSpotifyService;

    @Test
    @DisplayName("should return map of tracks to artist when artist appears more or five times")
    void shouldReturnMapOfTracksToArtist() {
        // given
        var currentDateTime = LocalDate.now();

        var random = new Random();

        when(spotifyWebService.fetchTracks())
                .thenReturn(Stream.of(
                        new Track("1", "1", "hwh1", "spotify:track:xyz1", "westside gunn", currentDateTime),
                        new Track("2", "2", "hwh2", "spotify:track:xyz2", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("3", "3", "hwh2", "spotify:track:xyz3", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("4", "1", "la machina", "spotify:track:xyz4", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("5", "2", "la machina", "spotify:track:xyz5", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("6", "1", "plugs i met", "spotify:track:xyz6", "benny the butcher", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("7", "4", "pray for paris", "spotify:track:xyz7", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("8", "5", "burden of proof", "spotify:track:xyz8", "benny the butcher", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("9", "6", "from king to a god", "spotify:track:xyz9", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("10", "7", "who made the sunshine", "spotify:track:xyz10", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("11", "8", "hwh3", "spotify:track:xyz11", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("12", "9", "hwh3", "spotify:track:xyz12", "westside gunn", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("13", "10", "la machina", "spotify:track:xyz13", "conway the machine", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("14", "11", "plugs i met 2", "spotify:track:xyz14", "benny the butcher", currentDateTime.plusDays(random.nextInt(30))),
                        new Track("15", "12", "god don’t make mistakes", "spotify:track:xyz15", "conway the machine", currentDateTime.plusDays(random.nextInt(30)))
                ));


        // when
        var artistSongMap = favoriteTrackSpotifyService.fetchAndOrganizeMyFavoriteTracksByArtistAndTrackCount();

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