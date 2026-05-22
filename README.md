# Tandem Community

Android submission for the Tandem hiring challenge: a paginated community feed backed
by [tandem2019.web.app](https://tandem2019.web.app/api/community_1.json).

## Overview

The app loads community members from `https://tandem2019.web.app/api/community_{page}.json` (pages
1–4, 78 members). Pagination stops when a page returns fewer than 20 items. Members with
`referenceCnt == 0` show a **NEW** badge. Tapping a member card toggles the like state; likes are
stored locally with DataStore and survive app restarts.

Network failures show user-facing messages with retry (full screen on first load, inline banner when
loading more pages).

## Tech stack

Kotlin · Jetpack Compose · Material 3 · Coroutines · ViewModel · Ktor 3 · Coil 3 · DataStore · Koin

## Build & run

**Prerequisites:** JDK 17, Android SDK 36 (minSdk 27)

```bash
./gradlew assembleDebug    # debug APK
./gradlew assembleRelease  # release APK (unsigned)
./gradlew test             # unit tests
```

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`  
Release APK: `app/build/outputs/apk/release/` (also available via GitHub Releases)

Open the project in Android Studio and run on an emulator or device.

## Tests

25 unit tests covering the API client, error mapping, repository, pagination use case, and
ViewModel (loading, errors, retry, likes).

| Test class                    | Focus                                            |
|-------------------------------|--------------------------------------------------|
| `CommunityApiTest`            | JSON parsing, `errorCode` in body, HTTP 404      |
| `CommunityErrorMapperTest`    | Offline / transport exceptions → `Network`       |
| `CommunityRepositoryImplTest` | DTO mapping, error propagation                   |
| `LoadCommunityUseCaseTest`    | Pagination, accumulation, end-of-list, `reset()` |
| `CommunityViewModelTest`      | UI state, load-more errors, likes, retry         |

## Architecture

```
presentation/   CommunityScreen, CommunityViewModel, CommunityContract
domain/         CommunityMember, CommunityRepository, use cases
data/           CommunityApi, CommunityErrorMapper, LikeLocalDataSource, DTOs
```

```
UI → ViewModel → Use case → Repository → API / DataStore
              ↑                    ↓
         CommunityState      DomainResult<CommunityError>
```

- **Pagination** — `LoadCommunityUseCase` holds the page index, accumulates members, and detects
  end-of-list (`PAGE_SIZE = 20`).
- **Likes** — `ObserveLikedIdsUseCase` exposes DataStore ids into `CommunityState.likedIds`; the
  list uses `state.isLiked(id)`.
- **Errors** — `CommunityApi` returns `DomainResult`; transport failures are mapped in
  `CommunityErrorMapper` (including `UnresolvedAddressException` from Ktor CIO on Android). Strings
  live in `strings.xml` and are resolved in `CommunityErrorUi.kt`.

## Project layout

```
app/src/main/java/com/asn/tandemcommunity/
├── di/AppModule.kt
├── data/remote/          CommunityApi, NetworkConstants, CommunityErrorMapper
├── data/local/           LikeLocalDataSource
├── data/repository/      CommunityRepositoryImpl
├── domain/               models, repository, use cases
└── presentation/         MainActivity, community screen, components, theme
```

## Notes

- Single-screen app; Navigation Compose was not required for the challenge scope.
- HTTP logging is enabled only in debug builds.
- Instrumented / Compose UI tests were not added; coverage is unit-test focused.
