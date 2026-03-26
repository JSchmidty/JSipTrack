# 🍺 SipTrack

**Real-time BAC tracking for iOS & Android — built with .NET MAUI**

![SipTrack Banner](https://via.placeholder.com/900x300/0D0D0D/4CAF50?text=SipTrack+%E2%80%94+Know+Your+Limit)

> *Drink smarter. Not harder.*

SipTrack is a privacy-first Blood Alcohol Content (BAC) tracker that uses the clinically validated **Widmark formula** to give you real-time BAC estimates, safe-to-drive countdowns, and a beautifully designed dashboard — all stored locally on your device.

Built for Sip Tech by Ace ♠️

---

## ✨ Features

- 📊 **Real-time BAC gauge** — animated circular speedometer, colour-coded Green / Amber / Red
- ⏰ **Safe-to-drive countdown** — know exactly when you're clear to drive
- 🍺 **Quick drink logging** — Beer, Wine, Shot, Cocktail, Micro-taste in one tap
- 🔍 **Beverage database search** — connects to SipTrack API (Node.js + PostgreSQL)
- 📱 **App Modes** — Normal, Discreet, Professional Tasting, Recovery, Designated Driver
- 🚨 **Emergency SOS** — one-tap SMS to emergency contact with location
- 🚗 **Uber deep link** — get a ride instantly when BAC is elevated
- 📈 **Session history** — view past sessions, peak BAC, drink timelines
- 🔥 **Streaks & stats** — drink-free day streaks, weekly summaries
- 💧 **Hydration reminders** — local push notifications
- 🔒 **100% private** — all drink data stored locally via SQLite (never leaves your phone)
- 📤 **CSV export** — export all your session data

---

## 📸 Screenshots

| Dashboard | Log Drink | History | Settings |
|-----------|-----------|---------|----------|
| *[Add screenshot]* | *[Add screenshot]* | *[Add screenshot]* | *[Add screenshot]* |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    SipTrack iOS/Android App                  │
│                        (.NET MAUI 8)                         │
├─────────────────┬───────────────────┬───────────────────────┤
│    Views        │    ViewModels      │      Services         │
│  (XAML + CS)    │  (MVVM Toolkit)   │   (Business Logic)    │
├─────────────────┼───────────────────┼───────────────────────┤
│ DashboardPage   │ DashboardVM       │ BACCalculatorService  │
│ LogDrinkPage    │ LogDrinkVM        │ DatabaseService       │
│ HistoryPage     │ HistoryVM         │ NotificationService   │
│ SettingsPage    │ SettingsVM        │ BeverageApiService    │
│ OnboardingPage  │ BaseViewModel     │                       │
└─────────────────┴───────────────────┴───────────────────────┘
                                │
                    ┌───────────┴────────────┐
                    │     Local SQLite DB     │
                    │  (sqlite-net-pcl)       │
                    │  UserProfile | Drinks   │
                    │  DrinkSessions          │
                    └─────────────────────────┘
                                │
                    ┌───────────┴────────────┐
                    │  Beverage API (opt.)    │
                    │  Node.js + PostgreSQL   │
                    │  /api/v1/beverages/*    │
                    └─────────────────────────┘
```

---

## 🧪 BAC Formula

SipTrack uses the **Widmark Formula** — the gold standard for BAC estimation:

```
BAC = (AlcoholGrams / (BodyWeightGrams × WidmarkR)) × 100
      - (MetabolicRate × HoursElapsed)
```

Where:
- `AlcoholGrams = VolumeOz × 29.5735 mL/oz × (ABV/100) × 0.789 g/mL`
- `BodyWeightGrams = WeightKg × 1000`
- `WidmarkR`: Male = 0.68, Female = 0.55, Other = 0.60
- `MetabolicRate`: default 0.015 BAC/hour (average adult)
- BAC is **never negative** — bottoms out at 0.000

**⚠️ Disclaimer:** BAC estimates are approximations only. Individual metabolism varies. Never rely solely on an app to determine fitness to drive.

---

## 🚀 Setup

### Mobile App (.NET MAUI)

**Prerequisites:**
- .NET 8 SDK
- Visual Studio 2022 17.8+ or JetBrains Rider
- Xcode 15+ (for iOS)
- Android SDK 34+ (for Android)

```bash
# Clone
git clone https://github.com/SipTechAI/JSipTrack.git
cd JSipTrack/SipTrack

# Restore packages
dotnet restore

# Run on iOS simulator
dotnet build -t:Run -f net8.0-ios

# Run on Android emulator
dotnet build -t:Run -f net8.0-android
```

### Backend API (Node.js)

```bash
cd JSipTrack/Backend

# Install dependencies
npm install

# Configure environment
cp .env.example .env
# Edit .env with your DATABASE_URL and API_KEY

# Start with Docker Compose (recommended)
docker compose up -d

# OR start manually (requires PostgreSQL running)
npm run dev
```

**API Endpoints:**
```
GET  /api/v1/health
GET  /api/v1/beverages/search?q=beer&category=beer&limit=20
GET  /api/v1/beverages/:id
POST /api/v1/beverages          (requires x-api-key header)
```

---

## 👤 App Modes

| Mode | Description |
|------|-------------|
| **Normal** | Full UI with all features |
| **Discreet** | Minimal UI — looks like a countdown timer |
| **Professional Tasting** | Micro-dose presets, exposure dashboard |
| **Recovery** | Streak counter, positive messaging, no BAC gauge |
| **Designated Driver** | DD badge, zero drinks auto-logged |

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feat/your-feature`
3. Make your changes following MVVM conventions
4. Add/update tests where applicable
5. Submit a PR to `main`

Please read `SETUP_REQUIRED.md` before submitting PRs.

---

## 📋 Required Setup

See [`SETUP_REQUIRED.md`](SETUP_REQUIRED.md) for:
- Apple Developer account & certificates
- iOS entitlements (HealthKit, Push Notifications)
- Android permissions
- Backend deployment configuration

---

## 📄 License

MIT License — Copyright (c) 2025 Sip Tech

See [LICENSE](LICENSE) for full text.

---

*Built with ♠️ by Ace at Sip Tech*

---

## 📱 iOS Setup (Compose Multiplatform)

> For full platform setup details see [`SETUP_REQUIRED.md`](SETUP_REQUIRED.md)

SipTrack uses **Kotlin Multiplatform + Compose Multiplatform** for iOS. The Swift layer is intentionally thin — all UI and business logic lives in Kotlin.

### Entry Point Architecture

```
SipTrackiOSApp.swift (@main)
  └── Koin DI init (KoinIosHelperKt.doInitKoin())
  └── ContentView.swift
        └── ComposeView (UIViewControllerRepresentable)
              └── MainViewControllerKt.MainViewController()
                    └── SipTrackApp() [shared Compose UI]
```

### Build Steps

**Prerequisites:** macOS, Xcode 15+, CocoaPods, JDK 17+

```bash
# 1. Generate KMP XCFrameworks (from project root)
./gradlew :shared:podPublishDebugXCFramework
./gradlew :composeApp:podPublishDebugXCFramework

# 2. Install pods
cd iosApp
pod install

# 3. Open workspace (always .xcworkspace, not .xcodeproj)
open iosApp.xcworkspace
```

Then in Xcode:
- Set your **Team ID** (Signing & Capabilities)
- Enable **HealthKit** capability
- Select a simulator or device → **⌘+R**

