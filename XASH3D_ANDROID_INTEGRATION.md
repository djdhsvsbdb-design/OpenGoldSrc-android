# Xash3D FWGS Android Integration

## Decision

The Android game client will use the official Xash3D FWGS engine as a separate engine integration. OpenGoldSrc remains in this repository as the original project; its code is not silently replaced.

## Verified upstream

- Official source: https://github.com/FWGS/xash3d-fwgs
- Android project: `android/` in the upstream repository.
- Upstream documents Android support for ARM and x86 targets, touch/gamepad input, OpenGL ES renderers, and GoldSrc protocol support.
- Upstream's Android instructions require Android Studio (or command-line tools), Python, Git, Ninja, and CMake.
- Upstream explicitly says to clone recursively; GitHub ZIP downloads omit external dependencies.
- Upstream additionally instructs checking out `3rdparty/nanogl` and `3rdparty/gl4es/gl4es`.
- For Android, upstream instructs using SDL from its `SDL2` branch and FWGS/HLSDK-portable from its `mobile-hacks` branch.

## Reproducible source checkout

Run these commands in a development environment with Git installed:

```sh
git clone --recursive https://github.com/FWGS/xash3d-fwgs
cd xash3d-fwgs
git submodule update --init --checkout 3rdparty/nanogl 3rdparty/gl4es/gl4es
```

Then follow the current Android build instructions in the upstream README. The SDL2 and HLSDK-portable checkouts must use the branches specified there. Open the upstream `android/` directory in Android Studio or build it with Gradle.

## Game data and multiplayer

The engine does not include proprietary Counter-Strike 1.6 game data. The user must supply legally obtained game files; this repository must not package Valve game assets without permission.

GoldSrc protocol support is a compatibility foundation, not proof that every public server will work. Validate server browser discovery, direct connect, authentication/protocol compatibility, map loading, and an actual multiplayer session against representative servers.

## Scope for this repository

1. Keep OpenGoldSrc and its Android bootstrap intact.
2. Integrate Xash3D as a clearly identified engine source/build target rather than copying isolated engine files.
3. Preserve upstream history and licenses; record the exact upstream commit and dependency revisions before pinning them.
4. Adapt the Android application shell and touch layout only after confirming the upstream Android app's package, activity, storage, and native-library interfaces.
5. Build APKs for supported ABIs and test on physical Android devices.

## Current limitation

The GitHub file-editing integration can create ordinary files but cannot create a Git submodule gitlink entry. Therefore this change records the verified upstream procedure; it does not pretend that the engine source has already been imported or built. The actual recursive clone and Android build must be performed in a Git/Android SDK environment.
