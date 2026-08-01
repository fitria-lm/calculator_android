# Kalkulator Neo - Android App
A modern Android calculator built with Jetpack Compose featuring Neo Brutalism design and Google Calculator-like behavior. The app provides a clean, responsive interface with live preview results, a calculation history panel, and full dark/light theme support. Designed for simplicity and speed, the application persists theme preferences and calculation history locally.

## Features
- **Neo Brutalism UI:** Hard shadows, thick borders, minimal corner radius, and a distinctive color palette.
- **Live Preview:** See calculation results in real-time as you type.
- **Smart Logic:** Behaves exactly like Google Calculator with proper state management (INPUT/RESULT_SHOWN).
- **History Panel:** Stores last 20 calculations, click to reuse expressions.
- **Dual Theme:** Light and Dark mode with persistent preference.
- **Custom Splash Screen:** Logo with fade-in animation.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material3)
- **State Management:** ViewModel + StateFlow
- **Persistence:** SharedPreferences
- **Min SDK:** Android 8.0 (API 24)
- **Target SDK:** Android 16 (API 36)

## Interface
|                 Light Theme                 |                Dark Theme                 |
|:-------------------------------------------:|:-----------------------------------------:|
| <img src="screenshots/light_theme.png" alt="Light Theme" height="400"> | <img src="screenshots/dark_theme.png" alt="Dark Theme" height="400"> |

## How to Run
### Option 1: Download APK
Download and install the latest APK directly on your Android device:
[Download APK](https://github.com/fitria-lm/calculator_android/releases/tag/v1.0.0)
### Option 2: Build from Source
1. Clone this repository:
   ```bash
   git clone https://github.com/fitria-lm/calculator_android.git
   cd calculator
   ```
2. Open the project in **Android Studio**.
3. Build and run:
   ```bash
   ./gradlew clean assembleDebug installDebug
   ```
4. The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## License
This project is licensed under the MIT License.

**Developed by:** Your Name
**Build Date:** August 2026
