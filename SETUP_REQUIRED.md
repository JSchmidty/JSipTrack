# SipTrack — Setup Required

This document lists all manual configuration steps that **cannot be automated**
and must be completed by a developer/team member before SipTrack can ship.

---

## 📱 iOS

### USER_TODO_1: Apple Developer Account

- Sign in to [developer.apple.com](https://developer.apple.com)
- Create/use an existing App ID: `ai.siptech.siptrack`
- Enable the following capabilities in your App ID:
  - ✅ HealthKit
  - ✅ Push Notifications
  - ✅ Associated Domains (for deep links)
  - ✅ Siri (optional, for future voice integration)

### USER_TODO_2: Bundle ID

Update in `SipTrack/SipTrack.csproj`:
```xml
<ApplicationId>ai.siptech.siptrack</ApplicationId>
```
Change to match your registered Apple Developer App ID.

### USER_TODO_3: Signing Certificate & Provisioning Profile

- Create a Distribution Certificate in Xcode or Apple Developer portal
- Create a Provisioning Profile for `ai.siptech.siptrack`
- Configure in Xcode / VS or update `.csproj`:
```xml
<CodesignKey>iPhone Distribution: Your Name (TEAM_ID)</CodesignKey>
<CodesignProvision>SipTrack AdHoc</CodesignProvision>
```

### USER_TODO_4: iOS Entitlements

Create/update `SipTrack/Platforms/iOS/Entitlements.plist`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" ...>
<plist version="1.0">
<dict>
    <key>com.apple.developer.healthkit</key>
    <true/>
    <key>aps-environment</key>
    <string>production</string>  <!-- or development -->
</dict>
</plist>
```

### USER_TODO_5: Info.plist Permissions (iOS)

Add to `SipTrack/Platforms/iOS/Info.plist`:
```xml
<key>NSHealthShareUsageDescription</key>
<string>SipTrack reads your weight data to calculate accurate BAC estimates.</string>
<key>NSHealthUpdateUsageDescription</key>
<string>SipTrack can log drink data to Apple Health.</string>
<key>NSLocationWhenInUseUsageDescription</key>
<string>SipTrack uses your location to provide emergency contact assistance.</string>
<key>NSContactsUsageDescription</key>
<string>SipTrack needs contacts access to add an emergency contact.</string>
```

### USER_TODO_6: Push Notification Certificates (iOS)

- Generate APNs certificate or APNs Auth Key in developer.apple.com
- Upload to your push notification provider (OneSignal, FCM, etc.)
- The app uses Plugin.LocalNotification (local only) — for remote push,
  additional server-side configuration is required.

---

## 🤖 Android

### USER_TODO_7: Android Permissions

Add to `SipTrack/Platforms/Android/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.SEND_SMS" />
```

### USER_TODO_8: Android Keystore (for release builds)

```bash
keytool -genkey -v -keystore siptrack.keystore -alias siptrack -keyalg RSA -keysize 2048 -validity 10000
```

Configure in `SipTrack/SipTrack.csproj` or `local.properties`:
```
KEYSTORE_FILE=siptrack.keystore
KEYSTORE_PASSWORD=yourpassword
KEY_ALIAS=siptrack
KEY_PASSWORD=yourpassword
```

### USER_TODO_9: Google Play App Signing

- Create an app in Google Play Console
- Enroll in Play App Signing
- Upload your release APK/AAB

---

## ⚙️ Backend API

### USER_TODO_10: PostgreSQL Database

Set up a PostgreSQL 15+ database and run the schema:
```bash
psql $DATABASE_URL < Backend/src/db/schema.sql
```

### USER_TODO_11: Environment Variables

Copy and fill in `Backend/.env.example`:
```bash
cp Backend/.env.example Backend/.env
```

Required:
- `DATABASE_URL` — PostgreSQL connection string
- `API_KEY` — Secret key for write operations (POST /beverages)

### USER_TODO_12: Backend Deployment

Options:
- **Docker Compose** (local/VPS): `docker compose up -d`
- **Railway / Render / Fly.io** — Push to your provider
- **AWS ECS / GCP Cloud Run** — Container-based deployment

Update `BeverageApiService.cs` base URL in app settings:
```csharp
// In MauiProgram.cs or appsettings:
builder.Services.AddSingleton(new BeverageApiService("https://api.yourdomain.com"));
```

---

## 🚨 Emergency SMS

### USER_TODO_13: SMS Permission (Android)

The emergency alert feature opens the native SMS composer.
On Android 13+, the `SEND_SMS` permission must be declared AND granted by the user.
Note: Direct SMS sending (without opening the composer) requires even more strict permissions.
SipTrack uses the safe approach of opening the native composer.

---

## ⌚ Apple Watch (Future)

### USER_TODO_14: WatchKit Extension

For Apple Watch companion:
- Create a new WatchKit App target in Xcode
- Add WatchConnectivity framework to both iOS and watchOS targets
- Implement `WCSession` delegate to sync BAC data
- Design watchOS complications for BAC display

---

## ✅ Pre-Launch Checklist

- [ ] Apple Developer account + Bundle ID registered
- [ ] iOS entitlements configured (HealthKit, push)
- [ ] Android permissions in AndroidManifest.xml
- [ ] Android release keystore generated
- [ ] Backend DATABASE_URL configured
- [ ] API_KEY set (strong random string)
- [ ] Backend deployed and accessible from mobile app
- [ ] App tested on real iOS device (not just simulator)
- [ ] App tested on real Android device
- [ ] BAC calculation validated against known values
- [ ] Emergency alert tested (real phone number)
- [ ] Privacy policy written and hosted
- [ ] Terms of service written and hosted
- [ ] App Store / Play Store developer accounts set up

---

## iOS Compose Multiplatform Setup

> Added by Ace ♠️ — Task #2: iOS Compose entry point wiring (2026-03-26)

### Step 1: Generate the KMP framework

From the project root on a Mac with Xcode installed:

```bash
./gradlew :shared:podPublishDebugXCFramework
./gradlew :composeApp:podPublishDebugXCFramework
```

### Step 2: Install CocoaPods dependencies

```bash
cd iosApp
pod install
```

### Step 3: Open in Xcode

```bash
open iosApp/iosApp.xcworkspace
```

> ⚠️ Always use `.xcworkspace` (not `.xcodeproj`) after running `pod install`.

### Step 4: Set Team ID

In Xcode: Select the `iosApp` target → **Signing & Capabilities** → **Team** → Select your Apple Developer Team.

### Step 5: Set Bundle ID

Change `com.siptech.siptrack` to your registered Bundle ID if different.

### Step 6: Enable HealthKit

In Xcode: Select the `iosApp` target → **Signing & Capabilities** → **+ Capability** → **HealthKit**.

### Step 7: Build and run

Select an iPhone simulator or device → **⌘+R**

---

## ✅ KMP iOS Entry Point — How It Works

```
Swift (ContentView.swift)
  └── ComposeView: UIViewControllerRepresentable
        └── MainViewControllerKt.MainViewController()   ← generated by KMP
              └── composeApp/src/iosMain/kotlin/…/MainViewController.kt
                    └── ComposeUIViewController { SipTrackApp() }
                          └── commonMain SipTrackApp.kt  (shared UI)
```

The `SipTrackApp.swift` `@main` entry initializes Koin DI via
`KoinIosHelperKt.doInitKoin()` before any view renders.
