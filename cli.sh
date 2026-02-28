#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Spotify Cherry Picking – CLI launcher
#
# This script:
#  1. Obtains a Spotify access token via the PKCE Authorization Code flow
#     (opens your browser once; no password or client-secret is stored).
#  2. Builds the JAR if needed.
#  3. Launches the interactive CLI using that token.
#
# Requirements: Java 21+, Maven wrapper (./mvnw), curl, python3, openssl
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

# ── Configuration ─────────────────────────────────────────────────────────────
# Read the Spotify client ID from application.yaml (same value used by the web server).
# Override by setting SPOTIFY_CLIENT_ID in your environment.
_YAML="src/main/resources/application.yaml"
if [[ -z "${SPOTIFY_CLIENT_ID:-}" ]]; then
  CLIENT_ID=$(grep -m1 'client-id:' "$_YAML" 2>/dev/null | awk '{print $2}' | tr -d '"' || true)
  if [[ -z "$CLIENT_ID" ]]; then
    echo "Error: could not read 'client-id' from ${_YAML}." >&2
    echo "Set the SPOTIFY_CLIENT_ID environment variable and try again." >&2
    exit 1
  fi
else
  CLIENT_ID="$SPOTIFY_CLIENT_ID"
fi
REDIRECT_URI="http://127.0.0.1:8888/callback"
SCOPES="user-library-read playlist-modify-private playlist-modify-public user-read-private"
CALLBACK_PORT=8888

# ── Helper: check required tools ──────────────────────────────────────────────
require() {
  if ! command -v "$1" &>/dev/null; then
    echo "Error: '$1' is required but not installed." >&2
    exit 1
  fi
}
require java
require curl
require python3
require openssl

# ── Step 1: Generate PKCE parameters ─────────────────────────────────────────
CODE_VERIFIER=$(openssl rand -base64 64 | tr -d '=+/\n' | cut -c1-128)
CODE_CHALLENGE=$(printf '%s' "$CODE_VERIFIER" \
  | openssl dgst -sha256 -binary \
  | openssl base64 \
  | tr '+/' '-_' \
  | tr -d '=')
STATE=$(openssl rand -hex 8)

ENCODED_SCOPES="${SCOPES// /%20}"
AUTH_URL="https://accounts.spotify.com/authorize\
?client_id=${CLIENT_ID}\
&response_type=code\
&redirect_uri=${REDIRECT_URI}\
&scope=${ENCODED_SCOPES}\
&state=${STATE}\
&code_challenge_method=S256\
&code_challenge=${CODE_CHALLENGE}"

# ── Step 2: Open browser ──────────────────────────────────────────────────────
echo "Opening Spotify authorization in your browser..."
echo "(If the browser does not open, copy and paste the URL below.)"
echo ""
echo "$AUTH_URL"
echo ""

if command -v xdg-open &>/dev/null; then
  xdg-open "$AUTH_URL" 2>/dev/null &
elif command -v open &>/dev/null; then
  open "$AUTH_URL" &
fi

# ── Step 3: Local callback server (captures the auth code) ───────────────────
echo "Waiting for Spotify to redirect back (listening on port ${CALLBACK_PORT})..."

# Python handles the one-shot HTTP callback cleanly
AUTH_CODE=$(python3 - <<'PYEOF'
import sys
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs

class _Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        parsed = urlparse(self.path)
        params = parse_qs(parsed.query)
        code = params.get("code", [None])[0]
        self.send_response(200)
        self.send_header("Content-Type", "text/html; charset=utf-8")
        self.end_headers()
        if code:
            body = b"<html><body><h2>Authorization complete!</h2><p>You can close this tab and return to your terminal.</p></body></html>"
        else:
            body = b"<html><body><h2>Authorization failed.</h2><p>No code received.</p></body></html>"
        self.wfile.write(body)
        self.server._code = code

    def log_message(self, *args):
        pass  # suppress HTTP log noise

server = HTTPServer(("127.0.0.1", 8888), _Handler)
server._code = None
server.handle_request()
print(server._code or "")
PYEOF
)

if [[ -z "$AUTH_CODE" ]]; then
  echo "Error: did not receive an authorization code from Spotify." >&2
  exit 1
fi

# ── Step 4: Exchange auth code for access token (PKCE – no client secret) ────
echo "Exchanging authorization code for access token..."

TOKEN_RESPONSE=$(curl -s -X POST "https://accounts.spotify.com/api/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "client_id=${CLIENT_ID}" \
  --data-urlencode "grant_type=authorization_code" \
  --data-urlencode "code=${AUTH_CODE}" \
  --data-urlencode "redirect_uri=${REDIRECT_URI}" \
  --data-urlencode "code_verifier=${CODE_VERIFIER}")

ACCESS_TOKEN=$(python3 -c "import json,sys; d=json.loads(sys.stdin.read()); print(d.get('access_token',''))" <<< "$TOKEN_RESPONSE")

if [[ -z "$ACCESS_TOKEN" ]]; then
  echo "Error: failed to obtain access token." >&2
  echo "Spotify response: $TOKEN_RESPONSE" >&2
  exit 1
fi

echo "Successfully authenticated with Spotify!"
echo ""

# ── Step 5: Build the JAR if it does not exist ───────────────────────────────
JAR=$(find target -maxdepth 1 -name "*.jar" ! -name "*-sources.jar" 2>/dev/null | head -1)
if [[ -z "$JAR" ]]; then
  echo "Building the application (first run may take a minute)..."
  ./mvnw -q package -DskipTests
  JAR=$(find target -maxdepth 1 -name "*.jar" ! -name "*-sources.jar" | head -1)
fi

# ── Step 6: Launch the CLI ────────────────────────────────────────────────────
SPOTIFY_ACCESS_TOKEN="$ACCESS_TOKEN" \
  java -jar "$JAR" --spring.profiles.active=cli
