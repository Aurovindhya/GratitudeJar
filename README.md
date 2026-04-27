# GratitudeJar

An Android app for building a daily gratitude practice. Write entries, fill your virtual jar, shake it open to revisit a random memory, and let location and reminders keep you grounded throughout the day.

## Features

- **Daily entries** - Write gratitude notes and add them to your personal jar
- **Random memory** - Shake or open the jar to surface a random past entry
- **Location tagging** - Attach your current location to each entry so you remember where you were
- **Reminders** - Schedule daily notifications to prompt your gratitude practice
- **Streaks** - Track consistency with a running streak counter
- **Cloud sync** - Entries are stored in Firebase so they persist across devices

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + XML Views |
| Storage | Firebase (Firestore / Realtime DB) |
| Location | Google Maps SDK, FusedLocationProvider |
| Notifications | AlarmManager, NotificationManager |
| Min SDK | Android 8.0 (API 26) |

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- A Firebase project with Firestore enabled
- A Google Maps API key

### Setup

1. Clone the repository

```bash
git clone https://github.com/Aurovindhya/GratitudeJar.git
cd GratitudeJar
```

2. Add your `google-services.json` from the Firebase console into `app/`

3. Add your Maps API key to `local.properties`

```
MAPS_API_KEY=your_key_here
```

4. Open in Android Studio and run on a device or emulator


## Roadmap

- [ ] Mood tagging on entries
- [ ] Weekly and monthly recap summaries
- [ ] Widget for home screen quick-entry
- [ ] Offline-first with background sync
- [ ] Share a gratitude note as an image

Copyright (c) 2026 Aurovindhya
