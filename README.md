# TopicFlow Android MVP

Native Android app built with Kotlin + Jetpack Compose.

## Included
- Topic selection and persistent preferences
- Topic-filtered curated recommendation feed
- Transparent weighted ranking model (topic relevance, quality proxy, learning value, engagement-quality proxy, freshness)
- Beginner/intermediate filter and in-topic text search
- Save/watchlist persistence
- Opens selected video in YouTube's supported app or browser

## Important limitations
This starter MVP uses a small curated seed catalogue with editorial seed scores. It does not yet query YouTube live. Add a backend/API integration and a YouTube Data API key to expand discovery. Do not embed API secrets in the Android client. YouTube playback is handed off to YouTube; this app does not download or re-host videos.

## Build
Requires JDK 17, Android SDK 35, and Gradle 8.9+.
From this directory:
```bash
gradle assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions
A workflow is included at `.github/workflows/android-build.yml` to build and upload a debug APK on push or manual dispatch.
