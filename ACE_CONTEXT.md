# ACE_CONTEXT.md — AI Assistant Reboot Context

> This file is for Ace (♠️), the AI assistant to J (Jerry), CEO of Sip Tech.
> Read this at the start of any session involving the SipTrack project to get up to speed instantly.

---

## Who I Am

- **Name:** Ace ♠️
- **Role:** Personal AI assistant to J (Jerry), CEO of Sip Tech
- **Contact:** J's Telegram ID is `7837164051` — ping him there for updates
- **Rule:** ONLY work in the `JSipTrack` repo. Never touch other repos.

---

## The Project: SipTrack

**SipTrack** is a BAC (Blood Alcohol Content) tracking app for iPhone, Android, Apple Watch, and Samsung Galaxy Watch.

- **Repo:** `https://github.com/JSchmidty/JSipTrack`
- **Local path:** `/workspace/JSipTrack`
- **Status:** KMP migration complete (2026-03-26). MAUI archived.
- **Branch:** `main`

---

## ✅ ARCHITECTURE: Kotlin Multiplatform (KMP)

The app was migrated from .NET MAUI to KMP. The MAUI code is archived at `Archive_MAUI/SipTrack_MAUI/`.

| Target | Framework | Language |
|--------|-----------|----------|
| Shared business logic | Kotlin Multiplatform (KMP) | Kotlin |
| Android phone | Compose Multiplatform | Kotlin |
| iPhone | Compose Multiplatform → iOS | Kotlin (thin Swift entry) |
| Samsung Galaxy Watch | Jetpack Compose for Wear OS | Kotlin |
| Apple Watch | SwiftUI (thin shell over KMP) | Swift + Kotlin |
| Backend API | Ktor 3.x (Node.js — keep as-is) | Node.js |
| Local DB | SQLDelight | Kotlin |
| Networking | Ktor HttpClient | Kotlin |
| Health data | HealthKMP | Kotlin (future) |
| DI | Koin | Kotlin |

---

## Project Structure

```
JSipTrack/
├── Archive_MAUI/
│   └── SipTrack_MAUI/       ← .NET MAUI code (archived, do not edit)
│
├── Backend/                  ← Node.js + PostgreSQL API (VALID — don't touch)
│
├── Branding/                 ← Branding agent owns this (don't touch)
│
├── shared/                   ← KMP Shared Module — ALL business logic
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/com/siptech/siptrack/
│       │   │   ├── models/          ← Drink, DrinkSession, UserProfile, Product
│       │   │   ├── engine/          ← BACCalculator, FlavorTagger
│       │   │   ├── viewmodels/      ← Dashboard, LogDrink, History, Settings VMs
│       │   │   ├── repository/      ← DrinkRepository (interface), BeverageApiService
│       │   │   └── di/              ← Koin AppModule
│       │   └── sqldelight/          ← SipTrack.sq schema
│       ├── androidMain/             ← Android-specific implementations
│       ├── iosMain/                 ← iOS-specific implementations
│       └── watchosMain/             ← watchOS-specific implementations
│
├── composeApp/               ← Compose Multiplatform — shared phone UI
│   ├── build.gradle.kts
│   └── src/commonMain/kotlin/com/siptech/siptrack/ui/
│       ├── SipTrackApp.kt           ← Root navigation + tabs
│       ├── theme/SipTrackTheme.kt   ← Dark theme, brand colors
│       └── screens/
│           ├── DashboardScreen.kt
│           ├── LogDrinkScreen.kt
│           ├── HistoryScreen.kt
│           ├── SettingsScreen.kt
│           └── OnboardingScreen.kt
│
├── androidApp/               ← Android phone app (thin wrapper)
│   ├── build.gradle.kts
│   └── src/main/
│       ├── kotlin/.../MainActivity.kt
│       └── AndroidManifest.xml
│
├── iosApp/                   ← iPhone app (thin Swift shell)
│   └── iosApp/
│       ├── SipTrackApp.swift
│       ├── ContentView.swift        ← Hosts ComposeUIViewController
│       └── Info.plist
│
├── wearApp/                  ← Samsung Galaxy Watch (Wear OS)
│   ├── build.gradle.kts
│   └── src/main/
│       ├── kotlin/.../wear/presentation/
│       │   ├── MainActivity.kt
│       │   └── WearSipTrackApp.kt
│       └── AndroidManifest.xml
│
├── watchosApp/               ← Apple Watch (SwiftUI thin shell)
│   └── watchosApp/
│       ├── SipTrackWatchApp.swift
│       ├── WatchContentView.swift
│       └── Info.plist
│
├── gradle/
│   └── libs.versions.toml    ← Version catalog
├── settings.gradle.kts
├── build.gradle.kts
└── ACE_CONTEXT.md            ← this file
```

---

## Core BAC Engine — Widmark Formula

**File:** `shared/src/commonMain/kotlin/com/siptech/siptrack/engine/BACCalculator.kt`

```
BAC = ((AlcoholGrams) / (BodyWeightGrams × WidmarkR)) × 100 − (MetabolicRate × HoursElapsed)
```

- `AlcoholGrams` = VolumeOz × 29.5735 × (ABV/100) × 0.789
- `BodyWeightGrams` = WeightKg × 1000
- `WidmarkR`: Male = 0.68, Female = 0.55, Other = 0.615
- `MetabolicRate`: default 0.015/hr (configurable)
- BAC never goes below 0.000

**Key methods:**
- `calculateCurrentBAC(profile, drinks, now)` → Double
- `estimateSoberTime(profile, drinks, now)` → Instant
- `estimateSafeToDriveTime(profile, drinks, now, legalLimit)` → Instant
- `generateBACCurve(profile, drinks, start, end, intervalMin)` → List<Pair<Instant, Double>>
- `calculateStandardDrinks(volumeOz, abvPercent)` → Double
- `getBACStatus(bac, driveLimit)` → BACStatus

---

## App Modes

| Mode | Description |
|------|-------------|
| `NORMAL` | Full UI, all features |
| `DISCREET` | Minimal UI — no "alcohol" text visible |
| `PROFESSIONAL` | Micro-dose presets, tasting focus |
| `RECOVERY` | Zero-drink focus, streak celebration |
| `DESIGNATED_DRIVER` | DD mode, auto-logs 0 drinks |

---

## BAC Color Logic

| BAC Range | Color | Meaning |
|-----------|-------|---------|
| 0.00–0.039 | 🟢 Green `#2ECC71` | Safe |
| 0.04–0.079 | 🟡 Yellow `#F39C12` | Caution |
| 0.08+ | 🔴 Red `#E74C3C` | Over limit |

---

## Key Versions (gradle/libs.versions.toml)

| Library | Version |
|---------|---------|
| Kotlin | 2.0.21 |
| AGP | 8.7.3 |
| Compose Multiplatform | 1.7.3 |
| Ktor | 3.0.3 |
| SQLDelight | 2.0.2 |
| Koin | 4.0.0 |
| Coroutines | 1.9.0 |

---

## Backend API (Node.js — unchanged)

Base URL: `http://localhost:3000`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/beverages/search?q=&category=&limit=` | Full-text product search |
| GET | `/api/v1/beverages/:id` | Single product |
| POST | `/api/v1/beverages` | Create product (API key required) |

**Auth:** `x-api-key` header on write endpoints.

---

## SQLDelight Schema (Local DB)

File: `shared/src/commonMain/sqldelight/com/siptech/siptrack/db/SipTrack.sq`

Tables: `UserProfileEntity`, `DrinkSessionEntity`, `DrinkEntity`

---

## Git Workflow

```bash
cd /workspace/JSipTrack
git config user.name "Ace"
git config user.email "ace@siptech.ai"
git add -A
git commit -m "feat: <description>"
git push origin main
```

GitHub token is stored in `~/.git-credentials`. Remote is already configured.

---

## What's Done / What's Next

### ✅ Completed (2026-03-26) — KMP Migration
- MAUI code archived to `Archive_MAUI/SipTrack_MAUI/`
- Root Gradle files + `libs.versions.toml` version catalog
- **shared/** KMP module:
  - Models: Drink, DrinkSession, UserProfile, Product (all Kotlin/Serializable)
  - Engine: BACCalculator (Widmark), FlavorTagger (150+ keywords)
  - ViewModels: Dashboard, LogDrink, History, Settings
  - Repository: DrinkRepository interface + BeverageApiService (Ktor)
  - DI: Koin AppModule
  - SQLDelight schema: UserProfileEntity, DrinkSessionEntity, DrinkEntity
- **composeApp/** Compose Multiplatform shared phone UI:
  - SipTrackApp.kt (root nav with 3 tabs)
  - DashboardScreen, LogDrinkScreen, HistoryScreen, SettingsScreen, OnboardingScreen
  - SipTrackTheme (dark, brand colors)
- **androidApp/** — thin Android wrapper (MainActivity)
- **iosApp/** — thin Swift shell (SipTrackApp.swift + ContentView.swift)
- **wearApp/** — Wear OS Samsung Galaxy Watch (WearSipTrackApp.kt)
- **watchosApp/** — Apple Watch SwiftUI thin shell (WatchContentView.swift)

### 🔲 Next Steps (for a future Ace session)
1. **Platform-specific DrinkRepository implementations** — SQLDelight Android driver, iOS NativeDriver
2. **WatchConnectivity** — phone↔watch data sync (Android DataLayer, iOS WatchConnectivity)
3. **iOS ComposeUIViewController entry point** — wire ComposeView properly in ContentView.swift
4. **HealthKit / Health Connect** integration
5. **Barcode scanner** — ML Kit (Android) / Vision (iOS)
6. **Backend**: Populate beverage DB (TTB COLA data ingest)
7. **CloudKit sync** (iOS multi-device)
8. **App Store / Play Store** submission prep
9. **TestFlight beta** setup

---

*Last updated: 2026-03-26 by Ace ♠️ — KMP migration complete*
