# Tennis Counter

Wear OS app for counting a tennis match.

## Sideload an APK

Every push to `main` runs the [Sideload APK](.github/workflows/sideload-apk.yml) workflow. You can also start it by hand from the Actions tab (`workflow_dispatch`).

The workflow runs `./gradlew test` and then builds an installable APK. Download it from the workflow run under **Artifacts**. The artifact name includes the short commit SHA, for example `tennis-counter-apk-cb7ee69`. Inside it, the file is `tennis-counter-debug-<sha>.apk` or `tennis-counter-release-<sha>.apk`.

### Install with adb

Enable developer options on the watch, then turn on **ADB debugging** (and **Debug over Wi-Fi** / **Wireless debugging** on newer Wear OS).

USB, when the watch is tethered through a phone or dock:

Replace `<file>.apk` with the APK you downloaded: `tennis-counter-debug-<sha>.apk` or `tennis-counter-release-<sha>.apk`.

```bash
adb devices
adb install -r <file>.apk
```

Wireless debugging:

1. On the watch, open **Settings > Developer options > Wireless debugging**.
2. Tap **Pair new device** and note the pairing address, port, and pairing code.
3. From the computer:

```bash
adb pair <watch-ip>:<pairing-port>
adb connect <watch-ip>:<connection-port>
adb devices
adb install -r <file>.apk
```

The connection port is the one shown on the Wireless debugging screen itself, not the temporary pairing port.

If the release signing secrets are not set, each CI run signs the debug APK with a different debug key. If a debug build from an earlier run is already installed, `adb install -r` fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Uninstall the old build first. This deletes the app's data on the watch:

```bash
adb uninstall com.aleksanderjess.tenniscounter
adb install <file>.apk
```

Release APKs signed with the same keystore secrets update in place.

### Optional release signing

No keystore is stored in this repository. If these four repository secrets are all set, the workflow builds a signed release APK. If any one of them is missing, it builds the debug APK instead. The debug APK is signed with the automatic debug key and can be installed with `adb install`.

| Secret | Value |
| --- | --- |
| `KEYSTORE_BASE64` | Base64 of the `.keystore` or `.jks` file (`base64 -w0 release.keystore`) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias inside the keystore |
| `KEY_PASSWORD` | Password for that key. For a PKCS12 keystore this is the same as `KEYSTORE_PASSWORD`. |

Create a keystore locally and add only the secrets above. Do not commit the keystore file.

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias tennis \
  -keyalg RSA -keysize 2048 -validity 10000
```
