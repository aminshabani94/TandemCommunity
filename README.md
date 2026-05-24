# Tandem Community

Android submission for the Tandem hiring challenge: a paginated community feed backed
by [tandem2019.web.app](https://tandem2019.web.app/api/community_1.json).

**Download APK (v1.0.0):
** [TandemCommunity.apk](https://github.com/aminshabani94/TandemCommunity/releases/download/v1.0.0/TandemCommunity.apk)  
**Releases:
** [github.com/aminshabani94/TandemCommunity/releases](https://github.com/aminshabani94/TandemCommunity/releases)

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
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew test
```

APK outputs:

| Variant | Path                                            | Install on device?                                 |
|---------|-------------------------------------------------|----------------------------------------------------|
| Debug   | `app/build/outputs/apk/debug/app-debug.apk`     | Yes                                                |
| Release | `app/build/outputs/apk/release/app-release.apk` | Yes (signed with debug keystore for local testing) |

To install on a connected device or emulator:

```bash
./gradlew installDebug
```

Or drag `app-debug.apk` onto the emulator.

**Note:** `assembleRelease` without a signing config produces `app-release-unsigned.apk`, which
cannot be installed (`INSTALL_PARSE_FAILED_NO_CERTIFICATES`). This project signs release builds with
the debug keystore so reviewers can install the release APK. For a production upload, create a
release keystore in Android Studio via **Build → Generate Signed Bundle / APK**.

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

## Architecture Decisions

### Why MVI over MVVM?

- Unidirectional data flow provides a single source of truth (`CommunityState`).
- All state changes flow through clear events (`CommunityEvent`), making behavior predictable.
- A single immutable state object reduces bugs and improves testability.
- Pagination state, loading states, and like toggles live in one place.
- Clear separation: events (inputs) → state reduction → UI (outputs).

### Why Ktor over Retrofit?

- Kotlin-native with a coroutine-first design.
- More idiomatic DSL for a greenfield Kotlin project.
- Built-in support for `kotlinx.serialization`.

### Why Koin over Hilt?

- Simpler setup for a single-module project.
- No annotation processing; faster build times for this scope.
- Readable DSL that fits a challenge-sized codebase.

### Why manual pagination over Paging 3?

- Keeps MVI state-reduction logic explicit and visible.
- Fully testable without library abstractions (`LoadCommunityUseCaseTest`).
- Page-size and end-of-list detection are clear in `LoadCommunityUseCase`.
- Demonstrates understanding of the underlying pagination problem.

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
- Release builds are signed with the debug keystore so the APK is installable for review.

## SOLID Principles Applied

Concrete examples from this codebase:

### Single Responsibility Principle

- `LoadCommunityUseCase` — pagination logic and member accumulation only.
- `CommunityRepositoryImpl` — coordinates network and local data sources only.
- `LikeLocalDataSource` — DataStore read/write for likes only.
- `CommunityViewModel` — UI state and event handling only.

### Open/Closed Principle

- `CommunityRepository` is an interface; implementations can be swapped without changing the
  ViewModel.
- `DomainResult` allows new failure types without modifying success-handling code paths.
- Use cases expose stable entry points; internal implementation can evolve independently.

### Liskov Substitution Principle

- `DomainResult` success and failure branches are handled uniformly in the ViewModel and use cases.
- `CommunityRepository` can be replaced with a mock in tests or a different production
  implementation without breaking callers.

### Interface Segregation Principle

- Use cases expose a minimal `invoke()` (or equivalent) surface; callers do not depend on internal
  pagination state such as the current page index.
- `CommunityRepository` defines focused methods: `getCommunity`, `toggleLike`, `observeLikedIds`.
- Callers depend on small contracts, not bloated interfaces.

### Dependency Inversion Principle

- `CommunityViewModel` depends on use cases and never on `CommunityRepositoryImpl` directly.
- The domain layer defines `CommunityRepository`; the data layer implements it.
- Domain has no dependencies on Android, Compose, Ktor, or DataStore.

## Feedback

Comments for the Tandem team about the challenge and the submission process (not a code
walkthrough).

**The task**

- The API shape (`response` wrapper, `natives` / `learns` arrays, `referenceCnt`) was clear once the
  first page was opened; a short sample JSON link in the brief would have saved initial setup time.
- Four pages with a short last page was a good way to test pagination without an oversized dataset.
- Like-on-card-tap plus persistence is a realistic, focused scope for a take-home.

**The process**

- I treated `develop` as the working branch and attached a signed installable APK via GitHub
  Releases for reviewers.
- Release builds are signed with the debug keystore so the APK installs without extra setup; a note
  in the brief that an unsigned `assembleRelease` APK cannot be installed on device would help
  future candidates.
- I focused unit tests on pagination, errors, and likes; UI/instrumented tests were out of scope for
  the time box I used.

**Assumptions**

- No design mock was provided; layout follows the challenge description and the public API’s member
  fields.
- Network errors are mapped to user-facing copy in the app; raw API `errorCode` values are not shown
  to users.
