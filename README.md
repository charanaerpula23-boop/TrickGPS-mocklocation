# TrickGPS 📍 | Android Mock Location App, Fake GPS Spoofer & Virtual Joystick

**TrickGPS** is a premium, open-source Android mock location application and GPS spoofer designed for developers, QA testers, and power users. It provides robust location simulation tools to test location-dependent apps, geofencing APIs, navigation systems, and route-based logic.

---

### 📥 Download Latest Release
[![Download APK](https://img.shields.io/badge/Download-Release_APK_v1.0-brightgreen?style=for-the-badge&logo=android&logoColor=white)](releases/trick-gps-v1.0-release.apk)  
*Or click here to browse files:* [Download trick-gps-v1.0-release.apk](releases/trick-gps-v1.0-release.apk)

---

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android SDK](https://img.shields.io/badge/SDK-29%2B-brightgreen.svg)](app/build.gradle.kts)
[![Platform](https://img.shields.io/badge/Platform-Android-orange.svg)](https://developer.android.com)

## 🌟 Key Features

*   **Static GPS Spoofing**: Pins any coordinate on an interactive map or searches locations to instantly fake your GPS position globally.
*   **Virtual GPS Joystick Overlay**: Manually shift coordinates in real time using a floating screen overlay with multi-speed support (Walk, Run, Cycle, Car).
*   **Route Simulation (Travel Mode)**: Simulates continuous driving/walking along actual roads. Select starting and ending points, and the app calculates the optimal path using **OSRM (Open Source Routing Machine)**.
*   **High-Precision Localized Search**: Tailored geocoding utilizing **OpenStreetMap Nominatim** to deliver extremely accurate search results (optimized specifically for Indian addresses, landmarks, and global locations).
*   **Background Mocking Service**: Uses an Android foreground service with a persistent system notification, ensuring mock location remains active when the app is minimized or the screen is locked.
*   **Premium Glassmorphic UI**: Designed with a modern, eye-catching Material 3 Jetpack Compose interface featuring glassmorphism cards, dynamic recentering, and optimized dark/light contrast.

## 🛠️ Setup Guide

To use TrickGPS for mock location testing, follow these simple steps:

1.  **Download and Install the APK**:
    *   Click the **Download Release APK** badge above, or download it directly from the [releases/](releases/trick-gps-v1.0-release.apk) directory.
2.  **Enable Developer Options**:
    *   Go to **Settings** → **About Phone**.
    *   Tap **Build number** `7` times until a notification says "You are now a developer!".
3.  **Set TrickGPS as the Mock Location App**:
    *   Go to **Settings** → **System** → **Developer options** (or search for "Developer options").
    *   Scroll down and tap **Select mock location app**.
    *   Choose **TrickGPS** from the list.
4.  **Start Spoofing**:
    *   Open **TrickGPS**, search for a destination or click on the map, and tap **Start Mocking**.

## 💻 Technical Architecture

*   **UI Framework**: Kotlin & Jetpack Compose
*   **Design System**: Material Design 3
*   **Map API**: `osmdroid` (for flexible, high-performance offline and online tile mapping)
*   **Routing API**: Open Source Routing Machine (OSRM) with custom User-Agent headers for request stability
*   **Geocoding**: Dual-provider integration (Google Play Services Geocoder & OSM Nominatim API for detailed search matches)
*   **Positioning Services**: Mock Location Provider via Android system location APIs

## 🚀 Building from Source

### Prerequisites
*   Android Studio Ladybug or newer
*   Android SDK 29+
*   Gradle 8.0+

### Windows Command Line
```cmd
gradlew.bat :app:assembleDebug
gradlew.bat installDebug
```

### macOS & Linux Command Line
```bash
./gradlew :app:assembleDebug
./gradlew installDebug
```

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.