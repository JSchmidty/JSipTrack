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

**SipTrack** is a BAC (Blood Alcohol Content) tracking app for iPhone and Apple Watch, built for Sip Tech.

- **Repo:** `https://github.com/JSchmidty/JSipTrack`
- **Local path:** `/workspace/JSipTrack`
- **Status:** Initial build complete and pushed (2026-03-26)
- **Branch:** `main`

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Mobile framework | **.NET MAUI** (C#, .NET 8) — targets iOS + Android |
| Architecture | **MVVM** with `CommunityToolkit.Mvvm` |
| UI | **MAUI XAML** + MAUI Graphics (custom controls) |
| Local database | **SQLite** via `sqlite-net-pcl` |
| Charts | **LiveChartsCore.SkiaSharpView.Maui** |
| Notifications | **Plugin.LocalNotification** |
| Backend API | **Node.js** (Express) + **PostgreSQL** |
| Backend auth | API key (`x-api-key` header) |
| Containerization | **Docker** / `docker-compose.yml` |

---

## Project Structure

```
JSipTrack/
├── SipTrack/                         # .NET MAUI app
│   ├── SipTrack.csproj               # NuGet deps, target frameworks
│   ├── MauiProgram.cs                # DI container setup
│   ├── App.xaml / App.xaml.cs        # App entry + first-run check
│   ├── AppShell.xaml / .cs           # Tab navigation shell (4 tabs)
│   ├── Models/
│   │   ├── Drink.cs                  # Drink entity, StandardDrinks calc
│   │   ├── DrinkSession.cs           # Session + AppMode enum
│   │   └── UserProfile.cs            # Weight, gender, Widmark R, limits
│   ├── ViewModels/
│   │   ├── BaseViewModel.cs          # INotifyPropertyChanged base
│   │   ├── DashboardViewModel.cs     # 60s timer, BAC refresh, commands
│   │   ├── LogDrinkViewModel.cs      # Quick presets + custom entry
│   │   ├── HistoryViewModel.cs       # Session groups, streak counter
│   │   └── SettingsViewModel.cs      # Profile save, reset
│   ├── Views/
│   │   ├── DashboardPage.xaml        # Main screen: gauge, BAC, stats, FAB
│   │   ├── LogDrinkPage.xaml         # Quick grid + custom form + search
│   │   ├── HistoryPage.xaml          # Session list grouped by week
│   │   ├── SessionDetailPage.xaml    # BAC curve chart + drink list
│   │   ├── SettingsPage.xaml         # All profile/prefs fields
│   │   └── OnboardingPage.xaml       # 3-step first-run flow
│   ├── Services/
│   │   ├── BACCalculatorService.cs   # Widmark formula engine (core logic)
│   │   ├── DatabaseService.cs        # SQLite CRUD, CSV export, reset
│   │   ├── NotificationService.cs    # Local push, emergency SMS, Uber link
│   │   └── BeverageApiService.cs     # HTTP client for backend search API
│   ├── Controls/
│   │   └── BACGaugeView.cs           # Custom IDrawable 270° speedometer
│   ├── Converters/
│   │   └── BACToColorConverter.cs    # BAC→Color, InverseBool, StringEqual
│   ├── Resources/
│   │   ├── Styles/AppStyles.xaml     # Dark theme (#0D0D0D), colors, fonts
│   │   └── Images/                   # Tab icons (home, plus, chart, gear)
│   └── Platforms/
│       ├── iOS/
│       │   ├── Info.plist            # NSHealthKit, camera, contacts keys
│       │   ├── Entitlements.plist    # HealthKit entitlement
│       │   └── AppDelegate.cs
│       └── Android/
│           ├── AndroidManifest.xml   # All required permissions
│           └── MainActivity.cs
│
├── Backend/                          # Node.js beverage DB API
│   ├── src/
│   │   ├── index.js                  # Express server, middleware, routes
│   │   ├── routes/beverages.js       # GET search, GET :id, POST create
│   │   └── db/
│   │       ├── connection.js         # pg Pool setup
│   │       └── schema.sql            # Tables + seed data
│   ├── package.json
│   ├── .env.example                  # All required env vars
│   ├── Dockerfile
│   └── docker-compose.yml            # App + Postgres services
│
├── README.md                         # Full project docs
├── SETUP_REQUIRED.md                 # 14 USER_TODOs before shipping
└── ACE_CONTEXT.md                    # ← this file
```

---

## Core BAC Engine — Widmark Formula

**File:** `SipTrack/Services/BACCalculatorService.cs`

```
BAC = ((AlcoholGrams) / (BodyWeightGrams × WidmarkR)) × 100 − (MetabolicRate × HoursElapsed)
```

- `AlcoholGrams` = VolumeOz × 29.5735 × (ABV/100) × 0.789
- `BodyWeightGrams` = WeightKg × 1000
- `WidmarkR`: Male = 0.68, Female = 0.55, Other = 0.60
- `MetabolicRate`: default 0.015/hr (configurable in Settings)
- BAC never goes below 0.000

**Key methods:**
- `CalculateCurrentBAC(profile, drinks, now)` → double
- `EstimateSoberTime(profile, drinks, now)` → DateTime
- `EstimateSafeToDriveTime(profile, drinks, now, legalLimit)` → DateTime
- `GenerateBACCurve(profile, drinks, start, end, intervalMin)` → IEnumerable<(DateTime, double)>
- `CalculateStandardDrinks(volumeOz, abvPercent)` → double
- `EstimateCalories(drinks)` → double (7 cal/gram of alcohol)

---

## App Modes (AppMode enum in DrinkSession.cs)

| Mode | Description |
|------|-------------|
| `Normal` | Full UI, all features |
| `Discreet` | Minimal UI, looks like a countdown timer — no "alcohol" text |
| `Professional` | Micro-dose presets (0.25, 0.5 standard drinks), separate weekly exposure dashboard |
| `Recovery` | Zero-drink focus, streak celebration, positive messaging, no BAC gauge |
| `DesignatedDriver` | Auto-logged as 0 drinks, shows "DD Mode Active" badge |

---

## BAC Color Logic

| BAC Range | Color | Meaning |
|-----------|-------|---------|
| 0.00–0.039 | 🟢 Green `#2ECC71` | Safe to drive |
| 0.04–0.079 | 🟡 Yellow `#F39C12` | Caution |
| 0.08+ | 🔴 Red `#E74C3C` | Do NOT drive (US legal limit) |

Drive limit is configurable (default 0.08 US, 0.05 in many other countries).

---

## Backend API Endpoints

Base URL: `http://localhost:3000` (configured via `DATABASE_URL` env)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/health` | Health check |
| GET | `/api/v1/beverages/search?q=&category=&limit=` | Full-text product search |
| GET | `/api/v1/beverages/:id` | Single product by UUID |
| POST | `/api/v1/beverages` | Create product (API key required) |

**Auth:** `x-api-key` header on write endpoints.

---

## Database Schema (PostgreSQL — Backend)

Key tables (see `Backend/src/db/schema.sql` for full DDL):
- **`products`** — beverages (id UUID, name, brand_id, category enum, abv, description, flavor_profile[], status)
- **`brands`** — brand directory (id, name, parent_company, country)
- **`cocktails`** — cocktail recipes (id, name, category, base_spirit, method, instructions, abv_estimated)
- **`cocktail_ingredients`** — join table with quantities and units
- **`ingredients`** — normalized ingredient dictionary

Category enum: `beer | wine | spirit | hard_seltzer | rtd_cocktail | cider | mead | sake | other`

---

## Local SQLite Schema (Mobile — DatabaseService.cs)

Tables: `UserProfile`, `DrinkSession`, `Drink`

Key relationships:
- Each `Drink` has a `SessionId` FK → `DrinkSession`
- `UserProfile` is a singleton (only one row, Id = 1)

---

## Quick Drink Presets (LogDrinkViewModel.cs)

| Name | ABV | Volume | Standard Drinks |
|------|-----|--------|----------------|
| Beer | 5% | 12 oz | ~1.0 |
| Wine | 12% | 5 oz | ~1.0 |
| Shot | 40% | 1.5 oz | ~1.0 |
| Cocktail | 15% | 4 oz | ~1.0 |
| Micro Taste | 40% | 0.5 oz | ~0.33 |

---

## Key User Personas (from design docs)

The app is designed for 25 personas. The most critical groups to keep in mind:

| Group | Persona Example | Key Needs |
|-------|----------------|-----------|
| Data nerds | Marcus Chen, Derek Nguyen | Charts, BAC curve, JSON export |
| Safety-conscious | Bob Fitzgerald, Miguel Santos | Large text, drive-safe countdown, simple UI |
| Professionals | Jorge Mendez (bartender), Sam Kowalski (sommelier) | Micro-dose mode, professional dashboard |
| Wellness trackers | Megan O'Brien, Aiden Murphy | HealthKit, weekly goals |
| Recovery users | Terrence Jackson | Zero-drink streak, no shame, positive tone |
| Accessibility | Simone Beaumont (paraplegic) | Voice Control, large tap targets, one-hand |
| Discreet users | Destiny Williams, Benny Okafor | Watch-first, hidden app, fake notification |
| Medical | Dr. Priya Sharma, Dr. Rebecca Liu | Clinical accuracy, data export, patient sharing |
| Breastfeeding | Dr. Fatima Al-Hassan | Breast milk clearance timer |

---

## SETUP_REQUIRED (Before Publishing to App Store)

Items needing human action (see `SETUP_REQUIRED.md` for full detail):

1. Apple Developer Account — enroll at developer.apple.com
2. Bundle ID — set in `SipTrack.csproj` (currently `com.siptech.siptrack`)
3. iOS Provisioning Profile — Xcode or manual
4. HealthKit entitlement — enable in Apple Developer portal
5. Push notification certificate — APNs setup
6. Android Keystore — for Play Store signing
7. Backend: set `DATABASE_URL` in `.env`
8. Backend: generate and set `API_KEY`
9. Emergency SMS — requires native contacts permission grant
10. Uber deep link — works with Uber app installed; no API key needed for basic launch

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

### ✅ Completed (2026-03-26)
- Full .NET MAUI app scaffolded (54 files)
- BAC Calculator (Widmark) fully implemented
- All 5 app modes implemented
- All 6 screens built with XAML
- Custom BAC gauge (BACGaugeView)
- SQLite database layer
- Emergency/safety features (SMS, Uber, notifications)
- Node.js + PostgreSQL backend with seed data
- Docker setup
- README + SETUP_REQUIRED docs

### 🔲 Possible Next Steps
- Apple Watch companion app (WatchKit / watchOS)
- Beverage barcode scanning (ML Kit / Vision)
- HealthKit read/write integration (actual Swift bridge or MAUI plugin)
- CloudKit sync for multi-device
- Populate beverage database (TTB COLA data ingest)
- UI polish / animations
- App Store submission prep
- TestFlight beta setup

---

*Last updated: 2026-03-26 by Ace ♠️*
