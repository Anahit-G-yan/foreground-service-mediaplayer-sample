# MediaPlayer

A local audio/video player for Android, built with **MVVM + XML views** on top of a
**multi-module Clean Architecture**. Originally a 2021 tutorial project, fully rebuilt to
current Android standards while keeping the MVVM/XML UI layer intentionally unchanged.

## Features

- Browse and play audio and video stored on the device (Music / Video tabs).
- Full playback controls: play/pause, seek, next/previous, repeat-one.
- Favorites, with embedded track artwork.
- Playback always runs through a Media3 foreground service with a system media notification,
  so it survives backgrounding and screen rotation.
- Empty-state placeholders when no music/video is found, and content descriptions on every
  playback control for screen-reader accessibility.
- Portrait-only.

## Tech stack

| Layer | Tooling |
|---|---|
| Language / build | Kotlin 2.2, AGP 8.13, Gradle 8.14, JDK 17, version catalog (`gradle/libs.versions.toml`) |
| UI | XML views, ViewBinding, Navigation Component, Material Components |
| Architecture | MVVM, Clean Architecture, multi-module Gradle project |
| Async | Kotlin Coroutines + `StateFlow`/`Flow` |
| DI | Hilt |
| Playback | Media3 (`ExoPlayer` + `MediaSessionService` + `MediaController`) |
| Persistence | Room (favorites), `MediaStore` (device audio/video) |
| Images | Glide (embedded artwork) |
| Testing | JUnit4, Turbine, `kotlinx-coroutines-test`, Room in-memory instrumented tests |
| Static analysis | ktlint, detekt |
| CI | GitHub Actions (ktlint, detekt, unit tests, assemble) |

## Architecture

```
app                   Application (Hilt), MainActivity, nav graph, composition root
domain                Pure Kotlin: models, repository interfaces, use cases
data                  Repository implementations: MediaStore, Room, mappers
core:common           Dispatchers, shared extensions
core:ui               Shared XML resources, ViewBinding helper, UI utilities
core:database         Room: FavoriteEntity/Dao/Database
core:media            Media3 playback: PlaybackService, PlayerController
feature:library       Music/Video tab lists (ViewPager2 + TabLayout)
feature:player        Music player screen
feature:videoplayer   Video playback screen (Media3 PlayerView)
```

Dependencies point strictly inward: `feature:*` depends on `domain` and `core:*`; `data`
depends on `domain`, `core:database`; `domain` has no Android dependency. `app` wires
everything together via Hilt and owns the single Navigation graph — `feature:*` modules
navigate to it via deep links (`core:ui`'s `NavDestinations`), since Safe Args classes aren't
reachable from modules that don't depend on `:app` under non-transitive R classes.

## Building and running

Requires JDK 17 and Android SDK (compileSdk/targetSdk 36, minSdk 24).

```bash
./gradlew :app:assembleDebug   # build
./gradlew :app:installDebug    # build and install on a connected device/emulator
```

## Testing

```bash
./gradlew testDebugUnitTest              # 39 unit tests (domain, data, core:media, core:ui, ViewModels)
./gradlew :core:database:connectedDebugAndroidTest   # instrumented Room DAO test (needs a device/emulator)
./gradlew ktlintCheck detekt             # static analysis
```

CI (`.github/workflows/ci.yml`) runs ktlint, detekt, unit tests, and an assemble build on
every push and pull request to `main`.
