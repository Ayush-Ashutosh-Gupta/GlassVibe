# GlassVibe

Work-in-progress Android widget suite for glassmorphic and liquid-style widgets.

GlassVibe is being built because good liquid/glass widgets are often:

- paid
- limited mainly to Apple-style ecosystems
- or only available through untrusted sideload sources

The goal is a cleaner Android-native option with free widgets, customization, and liquid physics that respond to real device motion.

> Status: Work in progress

## Concept

GlassVibe combines:

- a widget catalog inside the app
- glassmorphic visual style
- liquid widgets driven by accelerometer physics
- selected real home-screen `AppWidgetProvider`s
- customization controls for look and behavior

## Current direction

### In progress
- large widget catalog across many categories
- liquid physics engine
- glass-style rendering
- home-screen providers for selected widgets
- customization sheet / preview flow
- haptics and interaction polish

### Categories targeted
Liquid, Clocks, Weather, Screen Time, Battery, Music, Calendar, Launchers, Fitness, Notes, System, Quotes, Ambient, Crypto, Astronomy, Pomodoro, Habits, Network, Sensors, and more.

## Important WIP note

Not every catalog item is a fully independent production-grade home-screen widget yet.

The current architecture is:

1. rich in-app catalog and previews
2. liquid/glass rendering pipeline
3. selected providers for actual home-screen placement

That means the project should be viewed as an active widget platform in development, not a finished 100+ fully live widget store.

## Why this project matters

Android users often have to choose between:

- basic free widgets
- paid premium packs
- risky third-party downloads

GlassVibe aims to offer a trusted, modern, visually strong alternative with practical system widgets and expressive liquid/glass designs.

## Tech stack

- Kotlin
- Jetpack Compose
- Android App Widgets
- Sensor-driven liquid physics
- Custom canvas / bitmap rendering pipeline for widget surfaces

## Setup

### Requirements
- Android Studio
- Android device or emulator

### Run
1. Open the project in Android Studio
2. Sync Gradle
3. Install on device
4. Open the app, browse widgets, and pin supported widgets to the home screen

## Roadmap

- harden hero widgets first
- improve home-screen parity with in-app previews
- reduce catalog/provider gap
- polish performance on mid-range devices
- expand only after core widgets feel premium

## License

© 2026 Ayush Ashutosh Gupta. All Rights Reserved.

This repository is shared for portfolio and demonstration purposes only.  
No permission is granted to copy, modify, distribute, or use this code without explicit written consent.
