# trick-gps

**trick-gps** is an open-source Android mock location application designed for developers to test location-dependent behaviors. It supports static coordinate spoofing, route-based road travel simulation, and virtual joystick controls with a foreground service for persistent background sessions.

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android SDK](https://img.shields.io/badge/SDK-29%2B-brightgreen.svg)](app/build.gradle.kts)

## Features

- **Static GPS Spoofing**: Center the map or search coordinates to set a fixed mock location.
- **Route Simulation (Travel Mode)**: Set a starting and ending point to simulate continuous road travel with adjustable speeds.
- **Virtual Joystick**: Manually navigate and update mock coordinates in real time.
- **Random City Selector**: Automatically mock locations across pre-configured global cities.
- **Foreground Service**: Mock location sessions remain active when the app is minimized.
- **Offline Map View**: Leverages `osmdroid` for responsive offline satellite and street map rendering.

## Requirements

- Android 10 or higher (API level 29+)
- Developer Options enabled
- Selected as the active mock location app in developer settings

## Setup Guide

1. Install the APK on your device.
2. Enable Developer Options:
   * Go to **Settings** → **About phone**.
   * Tap **Build number** 7 times.
3. Select the mock location provider:
   * Go to **Developer Options** → **Select mock location app**.
   * Choose **trick-gps**.
4. Launch the application, search or pick coordinates, and tap **Start Mocking**.

## Technology Stack

- **Framework**: Jetpack Compose (Kotlin)
- **UI Components**: Material 3
- **Map Library**: osmdroid
- **Routing API**: OSRM (Open Source Routing Machine)
- **Geocoding**: Google Play Services Geocoder & OpenStreetMap Nominatim API

## Building and Installing

### Windows
```cmd
gradlew.bat :app:assembleDebug
gradlew.bat installDebug
```

### macOS / Linux
```bash
./gradlew :app:assembleDebug
./gradlew installDebug
```

## License
MIT License. See [LICENSE](LICENSE) for details.