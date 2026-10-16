#!/usr/bin/env python3
"""Build a signed release without passing passwords on the Gradle command line."""
import os
from pathlib import Path
import subprocess
import sys

root = Path(__file__).resolve().parent.parent
environment = os.environ.copy()
if not environment.get("ANDROID_RELEASE_STORE_FILE"):
    if sys.platform != "darwin":
        sys.exit("Set the four ANDROID_RELEASE_* signing variables; see RELEASING.md.")
    key = Path.home() / ".android/keystores/leaf-ir-remote.p12"
    if not key.is_file():
        sys.exit("Release key not found. Restore it from your private backup; see RELEASING.md.")
    result = subprocess.run([
        "security", "find-generic-password", "-a", "VishnuSrivatsava",
        "-s", "io.github.vishnusrivatsava.leafirremote.release-signing", "-w"
    ], capture_output=True, text=True)
    if result.returncode:
        sys.exit("Signing password unavailable in macOS Keychain. Unlock or restore the item.")
    password = result.stdout.rstrip("\n")
    environment.update({
        "ANDROID_RELEASE_STORE_FILE": str(key),
        "ANDROID_RELEASE_STORE_PASSWORD": password,
        "ANDROID_RELEASE_KEY_ALIAS": "leaf-ir-remote",
        "ANDROID_RELEASE_KEY_PASSWORD": password,
    })
required = ("ANDROID_RELEASE_STORE_FILE", "ANDROID_RELEASE_STORE_PASSWORD",
            "ANDROID_RELEASE_KEY_ALIAS", "ANDROID_RELEASE_KEY_PASSWORD")
if any(not environment.get(name) for name in required):
    sys.exit("All four ANDROID_RELEASE_* signing variables are required; see RELEASING.md.")
sys.exit(subprocess.call([
    "sh", str(root / "gradlew"), "--no-daemon", "testDebugUnitTest", "lintRelease", "assembleRelease"
], cwd=root, env=environment))
