# Spotify Cherry Picking

> Analyse your Spotify liked songs and automatically create **per-artist "Cherry Picked" playlists** containing only the tracks you've liked from each artist.

---

## What it does

1. **Fetches every track** in your Spotify "Liked Songs" library (handles pagination automatically).
2. **Groups tracks by artist** and **filters** to keep only artists for whom you have **5 or more** liked songs.
3. **Sorts each artist's tracks** by album release date (newest first).
4. **Creates a private Spotify playlist** named `Cherry Picked: <Artist Name>` for each qualifying artist and fills it with those tracks.

### Result

After running the tool you'll find playlists like:

```
Cherry Picked: Westside Gunn     ← 7 of your liked Westside Gunn tracks
Cherry Picked: Conway the Machine ← 5 of your liked Conway tracks
…
```

---

## How it works

The application is a **Spring Boot 3** service written in Java 21.

| Layer | Technology |
|---|---|
| HTTP / REST | Spring Web (Spring MVC) |
| Spotify API calls | Spring WebFlux `WebClient` |
| Authentication (web mode) | Spring Security OAuth 2.0 Authorization Code flow |
| Authentication (CLI mode) | OAuth 2.0 PKCE flow (no client secret sent) |

### Authentication explained

Spotify requires **user consent** before the app can read your library or create playlists.
Two modes are supported:

| Mode | Auth flow | Who opens the browser? |
|---|---|---|
| **Web server** | Standard Authorization Code (Spring Security handles everything) | You, manually |
| **CLI** | PKCE Authorization Code (`cli.sh` handles it automatically) | `cli.sh` opens it for you |

In both cases you are redirected to [accounts.spotify.com](https://accounts.spotify.com) **once** to click "Authorize".
After that, the app holds a short-lived access token and a refresh token to call the Spotify API on your behalf.

> **CLI and OAuth redirects**: A full CLI experience _is_ possible despite Spotify's redirect-based flow.
> `cli.sh` starts a tiny temporary HTTP server on `127.0.0.1:8888` to catch the redirect, extracts the
> authorization code, and exchanges it for an access token using PKCE – all without a client secret.
> Once the token is in hand the browser is no longer needed.

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java | 21+ |
| Maven | included via `./mvnw` |
| curl, python3, openssl | any recent version (CLI mode only) |
| Spotify Developer App | see setup below |

---

## Setup

### 1. Create a Spotify Developer App

1. Go to <https://developer.spotify.com/dashboard> and log in.
2. Click **Create app**.
3. Fill in any name/description.
4. Add **both** redirect URIs:
   - `http://localhost:8080/login/oauth2/code/spotify` ← web-server mode
   - `http://127.0.0.1:8888/callback` ← CLI mode
5. Save. Copy your **Client ID**.

### 2. Configure credentials

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          spotify:
            client-id: <YOUR_CLIENT_ID>
            client-secret: <YOUR_CLIENT_SECRET>   # only needed for web-server mode
```

> **Tip – CLI mode only**: The CLI uses PKCE, which does **not** require a `client-secret`.
> You only need to set `client-id` if you plan to use the CLI exclusively.

---

## Usage

### Option A – CLI (recommended)

The quickest way to get started. `cli.sh` handles everything end-to-end.

```bash
# First time: make the script executable
chmod +x cli.sh

# Run
./cli.sh
```

**What happens:**

1. Your browser opens to Spotify's authorization page.
2. You click **Authorize** once.
3. Spotify redirects to `127.0.0.1:8888/callback` – `cli.sh` catches it automatically.
4. The CLI menu appears in your terminal:

```
╔══════════════════════════════════════╗
║   Spotify Cherry Picking CLI         ║
╚══════════════════════════════════════╝

What would you like to do?
  1) Show my Spotify profile
  2) Show liked tracks grouped by artist (artists with ≥5 liked songs)
  3) Create cherry-picked playlists on Spotify
  4) Exit
>
```

Choose **3** to create the playlists. That's it.

#### Running the CLI manually (if you already have a token)

```bash
export SPOTIFY_ACCESS_TOKEN="<your_token>"
java -jar target/spotify-cherry-picking-*.jar --spring.profiles.active=cli
```

---

### Option B – Web server

The app exposes a small REST API secured by Spring Security OAuth2.

```bash
./mvnw spring-boot:run
```

Open <http://localhost:8080/home> in your browser.
Spring Security will redirect you to Spotify to authenticate.
Once logged in, use the following endpoints:

| Endpoint | Description |
|---|---|
| `GET /home` | Returns your Spotify user profile |
| `GET /playlists` | Lists your liked tracks grouped by artist (≥5 tracks) |
| `GET /create-cherry-picked-playlists` | Creates the cherry-picked playlists on Spotify |
| `GET /test` | Health-check endpoint |

Example using curl after authenticating in the browser (copy your `JSESSIONID` cookie from DevTools):

```bash
curl -b "JSESSIONID=<your_session_id>" http://localhost:8080/create-cherry-picked-playlists
```

---

## Project structure

```
src/main/java/com/example/spotifycherrypicking/
├── cli/
│   └── SpotifyCliRunner.java          # Interactive CLI (profile: cli)
├── config/
│   ├── CliWebClientConfig.java        # WebClient using Bearer token (profile: cli)
│   └── WebClientConfig.java           # WebClient using OAuth2 session (profile: !cli)
├── controller/
│   └── SpotifyController.java         # REST endpoints
├── model/
│   ├── domain/Track.java              # Internal track representation
│   └── spotify/                       # Spotify API DTOs
├── security/
│   └── SecurityConfig.java            # Spring Security OAuth2 setup (profile: !cli)
└── service/
    ├── CherryPickArtistService.java   # Orchestrates the playlist-creation workflow
    ├── FavoriteTrackSpotifyService.java # Fetch + group liked tracks
    ├── MeSpotifyService.java          # Fetch user profile
    ├── CreatePlaylisSpotifytService.java
    ├── AddTracksToPlaylistSpotifyService.java
    ├── SpotifyWebService.java          # Interface
    └── SpotifyWebServiceImpl.java      # Spotify API calls via WebClient
```

---

## Running tests

```bash
./mvnw test
```

---

## Spotify API scopes used

| Scope | Why |
|---|---|
| `user-library-read` | Read your Liked Songs |
| `user-read-private` | Read your user ID (needed to create playlists) |
| `playlist-modify-private` | Create/edit private playlists |
| `playlist-modify-public` | Create/edit public playlists |
