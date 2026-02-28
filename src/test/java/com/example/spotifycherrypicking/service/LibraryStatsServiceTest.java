package com.example.spotifycherrypicking.service;

import com.example.spotifycherrypicking.model.domain.LibraryStats;
import com.example.spotifycherrypicking.model.domain.Track;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryStatsServiceTest {

    @Mock
    private SpotifyWebService spotifyWebService;

    @InjectMocks
    private LibraryStatsService libraryStatsService;

    @Test
    @DisplayName("should compute total tracks, artists, qualifying artists, top artists and decades")
    void shouldComputeStats() {
        // given – 7 westside gunn, 5 conway, 3 benny  (benny doesn't qualify)
        when(spotifyWebService.fetchTracks()).thenReturn(Stream.of(
                track("1",  "westside gunn", 1995),
                track("2",  "westside gunn", 1996),
                track("3",  "westside gunn", 1997),
                track("4",  "westside gunn", 2000),
                track("5",  "westside gunn", 2001),
                track("6",  "westside gunn", 2010),
                track("7",  "westside gunn", 2011),
                track("8",  "conway the machine", 2000),
                track("9",  "conway the machine", 2001),
                track("10", "conway the machine", 2010),
                track("11", "conway the machine", 2011),
                track("12", "conway the machine", 2012),
                track("13", "benny the butcher", 2005),
                track("14", "benny the butcher", 2015),
                track("15", "benny the butcher", 2020)
        ));

        // when
        LibraryStats stats = libraryStatsService.fetchStats();

        // then
        assertThat(stats.totalTracks()).isEqualTo(15);
        assertThat(stats.totalArtists()).isEqualTo(3);
        assertThat(stats.qualifyingArtistsCount()).isEqualTo(2); // westside gunn + conway
        assertThat(stats.topArtists()).first().isEqualTo("westside gunn"); // most tracks
        assertThat(stats.topArtists()).containsExactlyInAnyOrder(
                "westside gunn", "conway the machine", "benny the butcher");

        // decades: 1990s (3), 2000s (5), 2010s (6), 2020s (1)
        assertThat(stats.tracksByDecade()).containsKeys("1990s", "2000s", "2010s", "2020s");
        assertThat(stats.tracksByDecade().get("1990s")).isEqualTo(3L);
        assertThat(stats.tracksByDecade().get("2000s")).isEqualTo(5L);
        assertThat(stats.tracksByDecade().get("2010s")).isEqualTo(6L);
        assertThat(stats.tracksByDecade().get("2020s")).isEqualTo(1L);
    }

    @Test
    @DisplayName("toDecadeLabel should return decade string")
    void shouldReturnDecadeLabel() {
        assertThat(LibraryStatsService.toDecadeLabel(1993)).isEqualTo("1990s");
        assertThat(LibraryStatsService.toDecadeLabel(2000)).isEqualTo("2000s");
        assertThat(LibraryStatsService.toDecadeLabel(2024)).isEqualTo("2020s");
    }

    private static Track track(String id, String artist, int year) {
        return new Track(id, "track-" + id, "album", "spotify:track:" + id, artist, LocalDate.of(year, 1, 1));
    }
}
