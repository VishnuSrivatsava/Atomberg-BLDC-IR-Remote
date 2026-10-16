# Releasing Leaf IR Remote

Application ID: `io.github.vishnusrivatsava.leafirremote`. The Java source namespace
remains `com.moba.atombergremote`; Android's application identity is separate.
The standalone app installs alongside upstream and our earlier debug-signed test
app. Upgrades must keep this application ID and the existing release signing key.
The first standalone release is v1.4.0, versionCode 6.

Release signing-certificate SHA-256 (public fingerprint):

```text
c1f06eaf1af3871a6798e0404bfe7aca4abc06ddfea6f3ef1df264a673c83504
```

## Private signing material

The maintainer's encrypted PKCS12 key is stored outside the checkout at
`~/.android/keystores/leaf-ir-remote.p12` (private file permissions), alias
`leaf-ir-remote`. The password is stored in macOS Keychain under service
`io.github.vishnusrivatsava.leafirremote.release-signing`, account
`VishnuSrivatsava`. No signing key or password is stored in GitHub or this repo.

**Keep a secure backup of both the encrypted keystore and its password.** Retrieve
the password through Keychain Access for a password-manager backup. Merely cloning
this source repository does not preserve the ability to sign future updates.
Do not create a replacement key if the original is unavailable: restore it first.

## Build

Use JDK 17 and Android SDK API 36; configure `JAVA_HOME` and `ANDROID_HOME` as needed.
On the maintainer's Mac:

```sh
python3 scripts/build-release.py
```

The script reads the password into memory and passes it to Gradle through the
environment, without placing it in command arguments or printing it. It runs
protocol tests, release lint, and `assembleRelease`.

On other machines, provide these four environment variables from private storage,
then use the same script:

- `ANDROID_RELEASE_STORE_FILE`: absolute path to the original keystore
- `ANDROID_RELEASE_STORE_PASSWORD`: keystore password
- `ANDROID_RELEASE_KEY_ALIAS`: `leaf-ir-remote`
- `ANDROID_RELEASE_KEY_PASSWORD`: signing-key password

Never put passwords in tracked files or paste them into issue/build logs. With no
signing variables, `sh gradlew assembleRelease` deliberately produces an unsigned
APK, so a source distributor such as F-Droid can sign its own build.

Signed output: `app/build/outputs/apk/release/app-release.apk`.

## Verify and publish

1. Confirm the protocol tests and release lint pass.
2. Use Android SDK `apksigner verify --verbose --print-certs` to verify signatures;
   compare the certificate SHA-256 fingerprint with the existing release key.
3. Use SDK `aapt dump badging` and `aapt dump permissions` to verify application ID,
   version, minSdk 24, release label, only TRANSMIT_IR permission, and no debuggable flag.
4. Install the release APK on a test phone; confirm launch and IR readiness. The new
   ID does not require uninstalling the old test app.
5. Commit the complete corresponding source, tag it (e.g. `v1.4.0`), and publish that
   exact APK with its SHA-256 checksum and signing-certificate fingerprint.
6. For future versions, increment versionCode, retain the same ID/key, and install
   with `adb install -r` to verify the update path.

GitHub signing and F-Droid signing may differ unless reproducible builds are set
up. Do not promise cross-channel updates or F-Droid availability before verification.
