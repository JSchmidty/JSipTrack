# SipTrack — UI Copy Guide
**Version 1.0 — SipTech Inc.**
*Last Updated: March 2026*

---

> **Ground rules:** Every string in this guide follows the Four Rules of SipTrack Copy: (1) never glamorize drinking, (2) never shame it, (3) 6th-grade reading level, (4) explain BAC on first use. All strings are neutral — they work equally for a person in recovery and a person actively tracking a session.

---

## PART 1: NAVIGATION LABELS

### Tab Bar (Bottom Navigation)

| Tab | Label | Icon State: Default | Icon State: Active |
|-----|-------|--------------------|--------------------|
| Main Dashboard | **Today** | gauge-outline | gauge-fill |
| Drink Logging | **Log** | plus-circle-outline | plus-circle-fill |
| Session History | **History** | clock-outline | clock-fill |
| Trends & Stats | **Insights** | chart-line-outline | chart-line-fill |
| Settings | **Settings** | gear-outline | gear-fill |

*Rationale: "Today" instead of "Dashboard" — more personal. "Log" instead of "Drinks" — works for all modes. "History" is universal. "Insights" vs. "Stats" — warmer, less clinical.*

---

### Main Navigation Bar Titles (Screen Titles)

| Screen | Navigation Title |
|--------|-----------------|
| Main dashboard | Today |
| Drink log sheet (modal) | Log a Drink |
| Beverage search | Search Beverages |
| Custom drink entry | Custom Drink |
| Session history list | History |
| Individual session detail | Session Detail |
| Insights / trends | Your Insights |
| Streak view (Recovery Mode) | Your Streak |
| Settings root | Settings |
| Body profile | Body Profile |
| Modes | App Mode |
| Limits & alerts | Limits & Alerts |
| Emergency contact | Emergency Contact |
| Medication list | Medications |
| Medication detail | Medication Detail |
| Medication search | Search Medications |
| Privacy & data | Privacy & Data |
| Export data | Export Your Data |
| Family circle setup | Family Circle |
| Family member detail | Shared With |
| About SipTrack | About |
| Disclaimer | Important Notice |
| Apple Watch | Apple Watch |
| Notifications | Notifications |
| BAC disclaimer full screen | About This Estimate |

---

### Action Sheet / Modal Titles

| Context | Title |
|---------|-------|
| Delete a drink log entry | Remove This Drink? |
| Delete a session | Delete This Session? |
| End session early | End Session? |
| Emergency SOS confirmation | Send Emergency Alert? |
| Export confirmation | Export Your Data |
| Age verification gate | Just Checking |
| Mode switch confirmation | Switch Mode? |
| Clear all data | Delete All Data? |
| Medication alert modal | Medication Notice |
| Safe-to-drive detail | About This Estimate |
| Session summary (morning after) | Last Night |
| Streak milestone | A Milestone |

---

### Settings Sections and Row Labels

**Section: Profile**
- Body Profile
  - Weight → "Your weight (used only for BAC calculation)"
  - Biological Sex → "Biological sex (affects how your body processes alcohol)"
  - Update Profile → "Update"

**Section: Mode**
- App Mode
  - Standard — "Full BAC tracking"
  - Discreet — "Number only — no context shown"
  - Recovery — "Streak-focused. No drink log required."
  - Designated Driver — "I'm not drinking tonight."
  - Professional Tasting — "Log small volumes precisely."

**Section: Safety**
- Personal Limit → "Alert me when I approach..."
- Emergency Contact → "Send an alert to..."
- Rideshare App → "Open this app when I tap Get a Ride"
- Medication List → "Your medications"

**Section: Notifications**
- Approaching Limit → On / Off
- Reached Limit → On / Off
- BAC Cleared → On / Off
- Session Summary (next morning) → On / Off
- Streak Milestones → On / Off
- Hydration Reminders → On / Off
- Weekly Summary → On / Off

**Section: Data & Privacy**
- Export Your Data → "Download as CSV"
- Delete All Data → "Permanently remove everything"
- Privacy Policy → "Read our privacy commitment"

**Section: Apple Watch**
- Show Complication → On / Off
- Complication Style → "Arc" / "Number" / "Both"
- Log from Watch → On / Off

**Section: About**
- About SipTrack
- Version → [version number]
- Disclaimer
- Terms of Use
- Privacy Policy

---

## PART 2: STATUS MESSAGES

*Every BAC status message must include: primary message, secondary message, safe-to-drive label, haptic recommendation.*
*BAC = Blood Alcohol Content. Explain this on first use within the onboarding flow.*

---

### BAC Status: 0.00% — Sober / Clear State

**Primary Message (large, Heading 2):**
Clear.

**Secondary Message (Body Regular, Text Secondary):**
Your estimated BAC is 0.00%. No alcohol detected in your system.

**Safe-to-Drive Indicator Label:**
Safe to drive ✓ (green checkmark)

**Haptic Recommendation:**
- On reaching 0.00% from above: `UINotificationFeedbackGenerator.success` — a clear, positive haptic signal
- On opening app when already at 0.00% (no active session): no haptic

---

### BAC Status: 0.01% – 0.039% — Below Limit, Safe Zone

**Primary Message:**
[BAC VALUE]%

**Secondary Message:**
You're below your limit. Estimated time to 0.00%: [TIME].

**Safe-to-Drive Indicator Label:**
Safe to drive ✓

**Haptic Recommendation:**
- On drink logged (entering this zone): `UIImpactFeedbackGenerator.medium`
- No additional haptic while in zone

---

### BAC Status: 0.04% – 0.079% — Approaching Limit

**Primary Message:**
[BAC VALUE]%

**Secondary Message:**
You're approaching your limit. Estimated time to 0.00%: [TIME].

**Safe-to-Drive Indicator Label:**
Approaching limit — plan ahead

**Haptic Recommendation:**
- On first entry into this zone (crossing 0.04%): `UINotificationFeedbackGenerator.warning` (two quick taps)
- On drink logged within zone: `UIImpactFeedbackGenerator.medium`

*Design note: Arc and number color shift to BAC Caution Amber. No alarmist language — the user is below the legal limit and has information to plan with.*

---

### BAC Status: 0.08% – 0.099% — Over Legal Limit

**Primary Message:**
[BAC VALUE]%

**Secondary Message:**
Your estimated BAC is at or above the legal limit of 0.08% for driving in most US states. Estimated time to 0.00%: [TIME].

**Safe-to-Drive Indicator Label:**
Do not drive

**Haptic Recommendation:**
- On first entry into this zone (crossing 0.08%): `UINotificationFeedbackGenerator.error` (distinctive pattern: short-short-long)
- Subtle pulse animation on gauge (1× only — not repeating)

*Design note: Factual, not panicked. The message informs. The haptic is distinctive so it registers physically without checking the screen. Color shifts to BAC Danger Red.*

---

### BAC Status: 0.10% – 0.149% — Significantly Impaired

**Primary Message:**
[BAC VALUE]%

**Secondary Message:**
At this level, coordination, judgment, and reaction time are significantly affected. Do not drive. Estimated time to 0.00%: [TIME].

**Safe-to-Drive Indicator Label:**
Do not drive

**Haptic Recommendation:**
- On entry into zone (crossing 0.10%): `UINotificationFeedbackGenerator.error`
- Medication warning check triggered automatically

---

### BAC Status: 0.15%+ — Severely Impaired

**Primary Message:**
[BAC VALUE]%

**Secondary Message:**
At this level, you may have difficulty with basic coordination. Drink water. Sit down. If you feel unwell, get help. Estimated time to 0.00%: [TIME].

**Safe-to-Drive Indicator Label:**
Do not drive

**Haptic Recommendation:**
- On entry (crossing 0.15%): `UINotificationFeedbackGenerator.error` repeated 3× with 1s interval
- Emergency SOS button surfaced prominently in this state (permanent until below 0.10%)

*Design note: This is the one context where the app provides brief, gentle guidance. Not lecturing — practical. "Drink water. Sit down." is actionable, not prescriptive about the drinking itself.*

---

### BAC Status: Unknown / Calculating

**Primary Message:**
—

**Secondary Message:**
Calculating your estimate...

**Safe-to-Drive Indicator Label:**
Estimating...

**Haptic Recommendation:**
None

*Design note: An animated ellipsis or pulsing indicator. No BAC number shown during loading. Do not show 0.00% as a placeholder — it could be misread as "clear."*

---

## PART 3: NOTIFICATION COPY

*All notifications follow Apple's notification format: title (bold) + body text. Keep total under 100 characters combined where possible.*

---

### Notification: Approaching Personal Limit

**Title:** Heads up.
**Body:** Your estimated BAC is approaching your personal limit of [LIMIT]%.

*Alternate (shorter):*
**Title:** Approaching your limit.
**Body:** Est. BAC: [VALUE]%. Limit: [LIMIT]%.

---

### Notification: Reached Personal Limit

**Title:** You've reached your limit.
**Body:** Est. BAC: [VALUE]%. Your personal limit is [LIMIT]%. Safe-to-drive est: [TIME].

---

### Notification: BAC Reached 0.00% (Sober)

**Title:** Back to zero.
**Body:** Your estimated BAC has cleared to 0.00%.

*Alternate:*
**Title:** Clear.
**Body:** Estimated BAC: 0.00%.

---

### Notification: Safe to Drive Now

**Title:** Safe to drive.
**Body:** Your estimated BAC has reached 0.00%. SipTrack estimates you're clear.

**Important footer (shown in expanded notification):**
*Estimates only. Actual BAC may differ. Use your judgment.*

---

### Notification: Medication Safety — BAC Cleared

**Title:** [Medication] — you're clear.
**Body:** Your BAC estimate is now 0.00%. [Medication] can generally be taken again.

**Important footer:**
*Confirm timing with your pharmacist or physician.*

---

### Notification: Session Summary Ready (Morning After)

**Title:** Last night, in brief.
**Body:** [NUMBER] drinks · [DURATION] · Peak BAC: [VALUE]%. Tap to see the full summary.

*Alternate (minimal):*
**Title:** Your session summary is ready.
**Body:** Tap to review last night's session.

*Send time: 8:00 AM local time, if a session ended the previous night.*

---

### Notification: Sobriety Streak Milestone

**1 Day:**
**Title:** Day 1.
**Body:** Your first day tracked. It counts.

**7 Days:**
**Title:** 7 days.
**Body:** One week. Yours.

**30 Days:**
**Title:** 30 days.
**Body:** A month, tracked day by day. That matters.

**90 Days:**
**Title:** 90 days.
**Body:** Three months of data, logged by you.

**1 Year:**
**Title:** One year.
**Body:** 365 days. Every single one counted.

*Design note: No exclamation points. No "sober" — the milestone is the number. Quiet, warm, understated.*

---

### Notification: Drink-Free Day Check-In

*(Optional, time configurable. Default: 9:00 PM if no drinks logged that day.)*

**Title:** Today's looking clear.
**Body:** No drinks logged yet today. Want to log a drink-free day? Tap to confirm.

*Alternate:*
**Title:** A clean day so far.
**Body:** No activity logged. Tap to mark today drink-free.

---

## PART 4: ONBOARDING COPY

### Step 1: Welcome Screen

**Headline (Display Large):**
Know Your Number.

**Body (Body Large):**
SipTrack tracks your BAC — your Blood Alcohol Content, the percentage of alcohol in your bloodstream — in real time. Log your drinks. Get clear data. Make your own calls.

No account. No server. Everything stays on your device.

**CTA Button:**
Get Started

**Secondary link (small, below button):**
Already have SipTrack? Restore from backup

---

### Step 2: Body Profile Setup

**Screen Title:** A quick setup

**Intro text (Body Regular):**
To estimate your BAC accurately, SipTrack needs two things: your body weight and your biological sex. This affects how alcohol is distributed in your body.

This information is stored on your device only. SipTrack never sends it anywhere.

---

**Field: Weight**
Label: Your weight
Placeholder: e.g., 155 lb or 70 kg
Unit toggle: lb / kg
Helper text: Used only to calculate your BAC estimate.
Accessibility label (VoiceOver): "Your body weight in pounds or kilograms — required for BAC calculation"

---

**Field: Biological Sex**
Label: Biological sex
Options: Male / Female / Prefer not to say
Helper text: Biological sex affects alcohol metabolism. "Prefer not to say" will use a blended average calculation.
Accessibility label: "Select your biological sex — used for BAC calculation. This information stays on your device."

*Note for Simone Test (accessibility):* All fields are reachable via Switch Control and VoiceOver. Weight field supports voice dictation. Picker options for biological sex must be reachable via accessibility swipe — not drag-only.

---

**CTA Button:** Continue
**Back link:** Back

---

### Step 3: Safety & Preferences

**Screen Title:** Set up your safety net

**Intro text:**
Two optional settings that make SipTrack more useful — and safer.

---

**Setting 1: Personal Limit**
Label: Your personal limit
Helper text: SipTrack will alert you when you approach this BAC level. The legal driving limit in most US states is 0.08%.
Default: 0.07% (one step below the legal limit — conservative default)
Input type: Slider (0.01 to 0.15, step 0.01) with numeric display
Note below slider: "This is your personal threshold — not a recommendation. Set it where it's useful for you."
Accessibility: Slider accessible via VoiceOver; numeric input field also available as alternative

---

**Setting 2: Emergency Contact**
Label: Emergency contact (optional)
Helper text: If you tap the SOS button in SipTrack, we'll send a text to this person with your location and a brief message. You control when and whether to use SOS.
Input: Name + phone number fields
Privacy note: "Stored on your device only. Never used without your tap."

---

**Disclaimer block:**
> SipTrack's BAC estimates are approximations and are not a substitute for a breathalyzer test or medical evaluation. Do not use this estimate to decide whether you are safe to drive. Individual factors — including metabolism, food intake, medications, and health conditions — significantly affect actual BAC. Always err on the side of caution.

**Checkbox (required before continuing):**
"I understand that SipTrack's BAC readings are estimates only and should not be used to make driving decisions."

**CTA Button:** Let's go
**Back link:** Back

---

## PART 5: ERROR & EMPTY STATES

### No Internet Connection

**Headline:** Working offline.
**Body:** SipTrack stores everything on your device — no internet needed for tracking. Some features (beverage search, medication database) require a connection. Everything else works normally.
**CTA:** Got it

---

### First Session Ever (Empty History State)

**Headline:** No sessions yet.
**Body:** When you log your first session, it'll appear here. Your full history — sessions, drink timelines, peak BAC — lives in this tab.
**CTA:** Log your first drink

---

### No Drinks Logged Today (Empty Active Session)

**Headline:** Nothing logged today.
**Body:** Log a drink and your BAC estimate will appear here. Tracking a drink-free day? Tap below.
**CTA 1 (primary):** Log a drink
**CTA 2 (secondary, text link):** Mark today drink-free

---

### Beverage Search: No Results Found

**Headline:** No results for "[SEARCH TERM]"
**Body:** Try a different name or brand. You can also log a custom drink with your own details.
**CTA:** Log a custom drink

---

### Medication Database: Drug Not Found

**Headline:** "[MEDICATION NAME]" isn't in our database.
**Body:** Our database includes 500+ medications. If yours isn't listed, log it manually — you'll still get reminders based on your BAC, just without specific interaction details.
**CTA 1:** Log it manually
**CTA 2 (text link):** Request this medication

---

### Data Export: Generating

**Headline:** Getting your data ready...
**Body:** This usually takes a few seconds. Your export will include all sessions, drink logs, and BAC estimates in CSV format.
*Progress indicator: animated Indigo activity indicator*

*On completion:*
**Headline:** Your data is ready.
**Body:** [NUMBER] sessions exported. Share or save the file below.
**CTA:** Share / Save to Files

---

## PART 6: LEGAL & DISCLAIMERS

### BAC Estimate Disclaimer
*(Appears adjacent to every BAC reading. Full version.)*

> **About this estimate:** SipTrack calculates your estimated Blood Alcohol Content (BAC) using the Widmark formula, based on your logged drinks, body weight, biological sex, and elapsed time. This is an estimate only. Your actual BAC may be higher or lower depending on individual factors including metabolism, food intake, hydration, medications, and health conditions. Do not use this estimate to determine whether you are safe to drive or operate machinery. Always err on the side of caution. SipTrack is not a medical device and this is not medical advice.

**Short-form (Apple Watch, inline small screens):**
> *Estimated BAC. Not medical advice. Do not use to decide to drive.*

---

### Medical Disclaimer
*(Appears in Settings → About, and in any screen featuring medication information.)*

> SipTrack is a personal health awareness tool, not a medical device. The BAC estimates provided by SipTrack are based on a mathematical formula and are approximations only. SipTrack's medication interaction database is for general reference only and is not a substitute for advice from a pharmacist, physician, or other qualified healthcare provider. Do not make medical decisions — including decisions about when to take medications — based solely on SipTrack's BAC estimates. Always consult a healthcare provider for medical guidance.

---

### Age Verification Gate Copy

**Screen Title (centered):** Just checking.

**Headline:** You need to be of legal drinking age to use SipTrack.

**Body:** SipTrack tracks Blood Alcohol Content and is designed for adults of legal drinking age. By continuing, you confirm that you are of legal drinking age in your country or region.

**Confirm button:** I confirm I'm of legal drinking age

**Decline link (text, small):** I'm not of legal drinking age

*On decline: App exits. No shame messaging — just: "SipTrack is designed for adults of legal drinking age. Thanks for being honest."*

---

### Privacy-First Messaging
*(For Settings → Privacy & Data screen)*

**Headline:** Your data belongs to you.

**Body:**

SipTrack was designed privacy-first from day one.

**What stays on your device:**
Everything you log — drink entries, session history, body profile, medications, emergency contact. None of this is transmitted to SipTech's servers. We do not collect it. We do not have access to it.

**What we do receive:**
If you purchase SipTrack Plus, your App Store or Google Play subscription is processed by Apple or Google — we receive confirmation that you have an active subscription, but not your payment details.

If you contact our support team, we receive the information you send us in that message only.

**Your controls:**
- Export all data: Settings → Privacy & Data → Export Your Data
- Delete all data: Settings → Privacy & Data → Delete All Data (this is permanent and cannot be undone)

**Analytics:**
We do not use third-party analytics tools. We do not use advertising SDKs. We do not track app usage patterns tied to your identity.

*This message is also available at siptech.ai/privacy*

---

*End of UI Copy Guide v1.0*
