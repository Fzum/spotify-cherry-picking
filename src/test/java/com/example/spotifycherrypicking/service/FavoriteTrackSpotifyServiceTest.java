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

    @Test
    @DisplayName("should group tracks by decade, sorted chronologically, filtering below minimum")
    void shouldGroupTracksByDecade() {
        // given – 3 tracks in 1990s, 4 in 2000s, 2 in 2020s (below MIN_TRACKS_PER_DECADE=3, filtered out)
        when(spotifyWebService.fetchTracks()).thenReturn(Stream.of(
                new Track("1",  "track-1", "album", "spotify:track:1",  "a", LocalDate.of(1993, 1, 1)),
                new Track("2",  "track-2", "album", "spotify:track:2",  "a", LocalDate.of(1995, 1, 1)),
                new Track("3",  "track-3", "album", "spotify:track:3",  "b", LocalDate.of(1999, 1, 1)),
                new Track("4",  "track-4", "album", "spotify:track:4",  "b", LocalDate.of(2001, 1, 1)),
                new Track("5",  "track-5", "album", "spotify:track:5",  "c", LocalDate.of(2003, 1, 1)),
                new Track("6",  "track-6", "album", "spotify:track:6",  "c", LocalDate.of(2007, 1, 1)),
                new Track("7",  "track-7", "album", "spotify:track:7",  "d", LocalDate.of(2009, 6, 1)),
                new Track("8",  "track-8", "album", "spotify:track:8",  "d", LocalDate.of(2021, 1, 1)),
                new Track("9",  "track-9", "album", "spotify:track:9",  "e", LocalDate.of(2023, 1, 1))
        ));

        // when
        var result = favoriteTrackSpotifyService.groupTracksByDecade();

        // then – 2020s has only 2 tracks (< MIN=3), must be filtered out
        assertThat(result).containsOnlyKeys("1990s", "2000s");
        assertThat(result.get("1990s")).hasSize(3);
        assertThat(result.get("2000s")).hasSize(4);
        // decades are in chronological (key) order
        assertThat(result.sequencedKeySet()).containsExactly("1990s", "2000s");
        // tracks within a decade are sorted newest-first
        assertThat(result.get("1990s").getFirst().albumReleaseDate()).isEqualTo(LocalDate.of(1999, 1, 1));
    }

}