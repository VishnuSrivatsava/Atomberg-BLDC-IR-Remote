# Atomberg IR Remote — native edition

A small, offline Android remote for Atomberg BLDC fans. Compact native controls,
light and dark themes, and no third-party runtime libraries. Only the Android IR
permission is requested: no accounts, ads, analytics, or network permission.

Derived from [Mobasheera/Atomberg-BLDC-IR-Remote](https://github.com/Mobasheera/Atomberg-BLDC-IR-Remote)
by **Mobashshir Ahsan**. Released under the original [MIT License](LICENSE).
See [NOTICE.md](NOTICE.md) for attribution and protocol research credits.

## Controls

- Power toggle; speeds 1–5; boost
- Timer and sleep, following the fan firmware's behavior
- LED toggle, with the corrected NEC code `0x6897F300`

Normal controls and LED have been confirmed on **Atomberg Renesa 1200 mm +
Realme GT 7T (Android 16)**. Other devices require an IR emitter exposed through
Android's ConsumerIrManager and support for a 38 kHz carrier. The app checks device
support, disables unavailable controls, and reports transmission failures.

IR is one-way. The app cannot read the fan's state or receive another remote's
signal, and does not display assumed on/off or speed selections.

## Changes from upstream

The fan command set and NEC timings are reused from the original project. This
version replaces its image overlay with a compact, accessible native interface,
adds device checks and error handling, and corrects the LED bit order. A published
LED capture was converted to LSB-first; we do not claim discovery of that protocol.
The About screen includes original author credit and the full MIT license.

## Build and test

Use JDK 17, Android SDK API 36, and the included Gradle wrapper:

```sh
sh gradlew testDebugUnitTest lintDebug assembleDebug
```

Set `ANDROID_HOME` or configure `sdk.dir` in an untracked `local.properties` file.
The debug APK is `app/build/outputs/apk/debug/app-debug.apk`. Debug signing is for
local testing; keep a private release signing key for public APK updates. Never
commit signing keys, passwords, or local device configuration.

[TESTING.md](TESTING.md) documents hardware tests, LED provenance, and limitations.
The IRplus profile in `remote-configs/Atomberg_Fan.irplus` also includes the LED fix.

## Sharing and contributing

This is already open-source code under MIT. Preserve LICENSE and attribution when
copying or distributing it. A public GitHub fork is the clearest way to retain
upstream history. Changes can also be proposed upstream as a pull request.

Source review found no tracking or networking code in this app. That does not
verify upstream APKs or guarantee compatibility with every device or fan.

Independent community project; not affiliated with Atomberg.
