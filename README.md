# FinalProject - Android App (SafeJet)

## Prerequisites

- **Java Development Kit (JDK) 17** (Required for Android Gradle Plugin)
- **Android Studio** (latest stable version recommended)
- **Git**

## Setup Instructions for Team Members

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd FinalProject
```

### 2. Open in Android Studio

- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned `FinalProject` directory
- Click "OK"

### 3. Let Android Studio Sync

- Android Studio will automatically:
  - Download the correct Gradle version (8.10.2) via the wrapper
  - Download all dependencies specified in `gradle/libs.versions.toml`
  - Set up the Android SDK components
- Wait for "Gradle sync" to complete (check bottom status bar)

### 4. Configure Android SDK (if prompted)

- If prompted about missing SDK platforms, click "Install missing platforms"
- Accept licenses if needed

### 5. Run the App

- Connect an Android device or start an emulator
- Click the green "Run" button (▶️) or press Shift+F10
- Select your device/emulator

## Build Versions

This project uses:

- **Gradle:** 8.10.2 (locked via wrapper)
- **Android Gradle Plugin:** 8.7.2
- **Kotlin:** 2.0.21
- **AndroidX Core KTX:** 1.15.0
- **AndroidX Activity:** 1.9.3
- **AndroidX AppCompat:** 1.7.0
- **Material Components:** 1.12.0
- **ConstraintLayout:** 2.2.1
- **JUnit:** 4.13.2
- **AndroidX JUnit:** 1.3.0
- **Espresso Core:** 3.7.0
- **Compile SDK:** 36
- **Target SDK:** 36
- **Min SDK:** 33
- **Java Compatibility:** 11 (Source/Target)
- **Build Environment JDK:** 17 (Required for AGP 8.7.2)

All dependency versions are centralized in `gradle/libs.versions.toml` to ensure consistency across all team members.

## Important Notes

### DO NOT Commit

- `local.properties` - Contains machine-specific paths
- `.idea/` folder contents (except what's needed)
- `build/` directories
- `*.iml` files

### DO Commit

- `gradlew` and `gradlew.bat` scripts
- `gradle/wrapper/` directory
- `gradle/libs.versions.toml`
- All `.gradle.kts` build files
- Source code and resources

## Troubleshooting

### "Gradle version mismatch" or build errors

1. Delete the `.gradle` folder in your project root
2. In Android Studio: File → Invalidate Caches → Invalidate and Restart
3. Let Gradle sync again

### "SDK not found" errors

1. Open Android Studio settings: File → Settings (or Preferences on Mac)
2. Go to Appearance & Behavior → System Settings → Android SDK
3. Install SDK Platform 36 (API 36) if not present

### Different Java versions

Ensure everyone uses JDK 11 or higher. Check your Java version:

```bash
java -version
```

## Contributing

1. Create a new branch for your feature: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Test thoroughly
4. Commit and push: `git push origin feature/your-feature-name`
5. Create a Pull Request

## Build Commands (Optional - Terminal Use)

### Build the project

```bash
# Windows
gradlew.bat build

# macOS/Linux
./gradlew build
```

### Run tests

```bash
# Windows
gradlew.bat test

# macOS/Linux
./gradlew test
```

### Clean build

```bash
# Windows
gradlew.bat clean build

# macOS/Linux
./gradlew clean build
```
