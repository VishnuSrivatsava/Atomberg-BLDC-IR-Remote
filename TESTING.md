# Leaf IR Remote verification

This checkout reuses the MIT-licensed repository's fan commands with native Android
buttons instead of the image overlay. It has no third-party runtime dependencies;
JUnit and AndroidX Test are test-only. Only `TRANSMIT_IR` is requested. No network,
account, storage, microphone, camera, contacts, analytics, or advertising code is
present in the app source. This is a source review, not a certification of upstream
release APKs, build tools, dependencies, or every fan/phone combination.

## Build

Use JDK 17 and an Android SDK supporting API 36. Set `ANDROID_HOME` or create
`local.properties` with `sdk.dir=/path/to/Android/sdk`, then run:

```sh
sh gradlew testDebugUnitTest lintDebug assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`. Debug signing is for local testing.
Keep the original MIT license when redistributing this code.

## Renesa 1200 mm + Realme GT 7T

Local verification: `testDebugUnitTest lintDebug assembleDebug` passed (5 tests,
0 failures; lint: 0 errors, 2 nonblocking warnings). The debug APK is 30,106 bytes,
its signature verifies, and its packaged manifest requests only `TRANSMIT_IR`.
Installed and opened on connected RMX5085 running Android 16; the screen reports
IR ready. The user confirms ordinary fan controls and the corrected LED toggle work.
The v1.2 compact interface was visually checked on this phone: all controls fit
on one screen. Scrolling remains available for smaller screens and larger fonts.
The packaged About license matches the original LICENSE byte for byte.
v1.3 adds an original vector fan launcher icon, adaptive and monochrome variants,
and vector fallbacks for Android 7. Lint and APK packaging passed; Android app info
on the Realme was visually checked to confirm the new icon renders correctly.
The icon-only update does not change the previously passing protocol code.

The user has verified normal controls and LED on Renesa 1200 mm + Realme GT 7T.
Other fan/phone combinations remain unverified by this checkout. The phone must expose its emitter through Android's `ConsumerIrManager`.
The app checks for an emitter and, when reported, carrier support at 38 kHz.
Unsupported devices show a message and disable transmission.

1. Connect the phone by USB, enable USB debugging, and accept the computer's key.
2. Check `adb devices -l`; install with `adb install app/build/outputs/apk/debug/app-debug.apk`.
   If this package is already installed with another signing key, stop rather than
   uninstalling it automatically. Uninstalling removes that app's local data.
3. Open Leaf IR Remote (or Atomberg IR Remote for historical builds). Keep the fan powered at its wall switch, point the
   phone's emitter toward it, and manually try power, then individual speeds.
4. Observe each response. A successful Android transmit call does not prove the
   fan received it. The app does not claim to know the fan's power/speed state.
5. Test timer/sleep separately; their behavior depends on the fan's own firmware.
6. Press LED on / off to toggle the indicators. Like power, it is a toggle; the
   app cannot determine whether the indicators are currently on. No scans or
   undocumented diagnostic commands are sent.

## LED provenance and limits

The first test preserved the bundled IRplus profile's LED signal `0xE916CF00`.
The user reports all ordinary controls work on the Renesa 1200 mm, but LED did not.

A [2025-02-22 capture comment by Pramod Ghuge](https://glenzac.wordpress.com/2020/03/25/atomberg-gorilla-efficio-bldc-fan-ir-remote-codes/)
reports LED as `0x00CFE916` in the older MSB-first notation. Reversing all 32 bits
for this app's LSB-first encoder gives **`0x6897F300`**, with address `0xF300` and
command `0x97`. Byte-swapping gives the failed `0xE916CF00` instead. Applying the
same bit reversal to the source's power `0x00CF8976` and timer `0x00CF6996` yields
the already working app codes. This is a supported correction, not a random scan.

The app and IRplus profile now use the corrected LED frame. Regression tests
check the published bit-order conversion and every profile waveform. The user has confirmed the corrected LED command works on the Renesa 1200 mm.
It is now a normal one-tap **LED on / off** button.
The original README's diagnostic `1/2/6 LED` commands are not exposed.

ADB can inspect Android support and logs, but the phone's IR transmitter cannot
receive and decode another remote. For other models where this command fails, compare the original remote's LED
button and capture it with a 38 kHz IR receiver if available.

## Standalone release

v1.4.0 uses application ID `io.github.vishnusrivatsava.leafirremote` and the public
name Leaf IR Remote. Release builds use a dedicated private signing key, not the
Android debug key. See RELEASING.md for the build and verification procedure.
The protocol and controls remain the hardware-verified implementation above.

The v1.4.0 release build and all five protocol tests passed; release lint reported
0 errors and 2 tool-version warnings. The APK signature verifies under the
dedicated 3072-bit RSA key, its manifest uses the new ID and Leaf IR Remote label,
requires Android API 24+, requests only TRANSMIT_IR, and has no debuggable flag.
The bundled MIT notice remains byte-for-byte identical to LICENSE.
