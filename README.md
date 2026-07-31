# 🧮 Calculator Neo

Modern Neo Brutalism Calculator built with Jetpack Compose.

[![Download APK](https://img.shields.io/badge/Download-APK-FFD60A?style=for-the-badge&logo=android)](https://github.com/yourusername/calculator/releases/latest)

---

## 📸 Screenshots

| Light Theme | Dark Theme |
|:-----------:|:----------:|
| ![Light Theme](./screenshots/light_theme.png) | ![Dark Theme](./screenshots/dark_theme.png) |

---

## ✨ Features

- **Basic Operations** – `+`, `-`, `×`, `÷`, `%`, `±`, parentheses
- **History** – stores last 20 calculations, click to reuse
- **Dual Theme** – Light & Dark with persistent preference
- **Neo Brutalism UI** – hard shadow, thick border, minimal corner radius
- **Custom Splash Screen** – logo, fade-in animation, auto transition
- **Keyboard Support** – full keyboard input for quick use
- **Responsive** – adapts to all screen sizes

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose (Material3) |
| State | ViewModel + StateFlow |
| Persistence | SharedPreferences |
| Font | Poppins (Google Fonts) |
| Min SDK | Android 8.0 (API 24) |
| Target SDK | Android 16 (API 36) |

---

## 📁 Project Structure

```
app/
├── manifests/
│   └── AndroidManifest.xml
│
├── kotlin+java/
│   └── com.example.calculator/
│       ├── MainActivity.kt
│       ├── SplashActivity.kt
│       ├── ui/
│       │   ├── screens/
│       │   │   └── CalculatorScreen.kt
│       │   ├── theme/
│       │   │   ├── Color.kt
│       │   │   ├── Theme.kt
│       │   │   └── Type.kt
│       │   └── viewmodels/
│       │       ├── CalculatorViewModel.kt
│       │       └── CalculatorViewModelFactory.kt
│       │
│       ├── com.example.calculator (androidTest)
│       └── com.example.calculator (test)
│
├── res/
│   ├── drawable/
│   │   ├── ic_calculator_logo.xml
│   │   └── ic_launcher.png
│   ├── font/
│   ├── mipmap/
│   ├── values/
│   └── xml/
│
└── Gradle Scripts/
    ├── build.gradle.kts (Module: app)
    ├── build.gradle.kts (Project: calculator)
    ├── settings.gradle.kts
    └── ...
 
```

---
## 🚀 How to Run
### Prerequisites
- Android Studio (latest)
- Android SDK 34+
- Java 11+

### Steps
```bash
# Clone repository
git clone https://github.com/yourusername/calculator.git
cd calculator

# Build Debug APK
./gradlew clean assembleDebug

# Install to connected device
./gradlew installDebug
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📥 Download

| Version | Download |
|---------|----------|
| Latest Release | [Download APK](https://github.com/yourusername/calculator/releases/latest) |
| Debug Build | `./gradlew assembleDebug` |

---

## 🎨 Design Notes

- **Style**: Neo Brutalism – hard shadows, thick borders (3dp), minimal rounding
- **Shadow**: Solid color, offset (6dp, 6dp), no blur
- **Colors**: `#FFD60A` (primary), `#FF9800` (secondary)
- **Font**: Poppins (fallback to system font)

---

## 📝 License

MIT License – bebas dipakai, dimodifikasi, dan didistribusikan.
