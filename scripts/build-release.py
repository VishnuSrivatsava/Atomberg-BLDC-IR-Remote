#!/usr/bin/env python3
"""Build a signed release without passing passwords on the Gradle command line."""
import os
from pathlib import Path
import subprocess
import sys

root = Path(__file__).resolve().parent.parent
environment = os.environ.copy()
required = ("ANDROID_RELEASE_STORE_FILE", "ANDROID_RELEASE_STORE_PASSWORD",
            "ANDROID_RELEASE_KEY_ALIAS", "ANDROID_RELEASE_KEY_PASSWORD")
if any(not environment.get(name) for name in required):
    sys.exit("All four ANDROID_RELEASE_* signing variables are required; see RELEASING.md.")
sys.exit(subprocess.call([
    "sh", str(root / "gradlew"), "--no-daemon", "testDebugUnitTest", "lintRelease", "assembleRelease"
], cwd=root, env=environment))
