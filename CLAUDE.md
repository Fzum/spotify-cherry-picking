# CLAUDE.md

This file provides guidance for Claude Code when working with this repository.

## Project Overview

**Spotify Cherry Picking** is a Spring Boot 3 / Java 21 application that analyses a user's Spotify Liked Songs and automatically creates per-artist "Cherry Picked" playlists (only for artists with 5+ liked tracks).

## Build & Test Commands

```bash
# Run all tests
./mvnw test

# Build the project (skip tests)
./mvnw package -DskipTests

# Build and run (web mode – opens browser for OAuth)
./mvnw spring-boot:run

# Run via CLI (handles PKCE OAuth automatically)
./cli.sh
```

## Architecture

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.1, Java 21 |
| HTTP/REST | Spring Web (MVC) |
| Spotify API calls | Spring WebFlux `WebClient` |
| Auth (web mode) | Spring Security OAuth 2.0 Authorization Code |
| Auth (CLI mode) | OAuth 2.0 PKCE flow via `cli.sh` |

### Key packages

- `controller/` — REST endpoints (`/home`, `/playlists`, `/create-cherry-picked-playlists`)
- `service/` — Core business logic
  - `CherryPickArtistService` — orchestrates playlist creation/deletion
  - `FavoriteTrackSpotifyService` — fetches & groups liked tracks by artist (≥5 tracks filter)
  - `SpotifyWebService` / `SpotifyWebServiceImpl` — low-level Spotify API calls
  - `CreatePlaylisSpotifytService`, `AddTracksToPlaylistSpotifyService`, `MeSpotifyService` — focused Spotify operations
- `model/spotify/` — DTOs for Spotify API responses
- `model/domain/` — Internal domain models (e.g. `Track`)
- `config/` — `WebClient` configuration (web vs. CLI auth contexts)
- `security/` — Spring Security configuration

## Configuration

Spotify credentials are read from `src/main/resources/application.yaml`. The CLI script also reads `client-id` from that file (or `SPOTIFY_CLIENT_ID` env var).

Required Spotify app settings:
- Redirect URI: `http://127.0.0.1:8080/login/oauth2/code/spotify` (web mode)
- Redirect URI: `http://127.0.0.1:8080/cli/callback` (CLI mode)
- Scopes: `user-library-read`, `playlist-modify-private`, `playlist-read-private`

## Testing

Tests live in `src/test/java/`. Key test classes:
- `CherryPickArtistServiceTest` — unit tests for playlist orchestration
- `FavoriteTrackSpotifyServiceTest` — unit tests for track grouping/filtering logic

Run tests with `./mvnw test`.
