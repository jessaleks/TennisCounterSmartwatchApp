# Agent environment notes

## JDK

- `/Library/Java/JavaVirtualMachines/openjdk-17.jdk` is a **broken symlink**
  (Homebrew `openjdk@17` keg was uninstalled). Do not use it.
- Working JDK for this repo:
  `export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home`
  Gradle 9.8.2 runs fine on it; `sourceCompatibility`/`targetCompatibility`
  stay 17.

## Build / test commands

```
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew compileDebugAndroidTestKotlin
./gradlew connectedDebugAndroidTest
```

## Devices

- Instrumented tests run on every connected device, including the physical
  watch when `192.168.101.29:46555` is attached — the installed app is signed
  with this machine's local debug key (`5b24d3…`, installed by lead after a
  user-authorized reinstall), so `adb install -r` updates work; still,
  disconnect the watch (`adb disconnect 192.168.101.29:46555`) before
  `connectedDebugAndroidTest` so tests don't install on it. Note that the
  test task uninstalls the app APKs from the device when it finishes.
- Emulator AVDs: `TennisCounter_Wear_Round` (wearos_small_round, Wear OS 6.0,
  `system-images;android-36;android-wear-signed;arm64-v8a`, 384x384@320dpi)
  — boot with `emulator -avd TennisCounter_Wear_Round -port 5556
  -no-snapshot-save`. Phone AVDs `Pixel_API_36_16k`, `Pixel_7_API_34` also
  exist but the app needs the debug-manifest `com.google.android.wearable`
  overlay to install on them.
- The app UI is Polish by default; strings live in `res/values/strings.xml`.
- The debug APK declares `uses-library com.google.android.wearable
  required="true"` (main manifest); `app/src/debug/AndroidManifest.xml`
  relaxes it to `required="false"` for debug builds only so instrumented
  tests can run on phone emulators.

## Evidence

- Review artifacts and screenshots: `.devin/review/`.
