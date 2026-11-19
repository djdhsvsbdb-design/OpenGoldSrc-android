# Android Port Status

## Initial repository audit

Audit date: 2026-11-19

This branch is the starting point for investigating an Android client port. It does not yet contain an Android application or a working Android build.

Findings from the current `master` snapshot:

- The repository README describes OpenGoldSrc as a recreation of the GoldSrc engine, not a complete Counter-Strike 1.6 distribution.
- The top-level build uses CMake and adds `dep/` and `ogs/`.
- `ogs/CMakeLists.txt` currently enables `tier1`, `dedicated`, `hookers`, `launcher`, `rehlds`, `tier0`, `vgui`, and `vstdlib`.
- The client-facing `engine`, `filesystem`, `game`, `render`, `sound`, and `voice` modules are commented out in that build file.
- No Android Gradle project, Android manifest, NDK toolchain configuration, or Android packaging workflow was found at the expected root paths.
- The README says build and installation instructions are TODO.
- The README identifies GPLv3 but also explicitly warns that parts of the source are derived from material not officially open-sourced by Valve. License and provenance of each component must be reviewed before distributing an APK.

## Consequence

A build-system change alone cannot turn this snapshot into a playable CS 1.6 Android client. The current configured targets appear focused on dedicated/server-side components; the client, renderer, audio, input, and Android application layers are not enabled by the top-level build.

## Port plan

1. Map the complete source tree and identify which client, renderer, filesystem, audio, input, and UI modules are actually present.
2. Verify submodule/dependency availability and review per-component licenses and provenance.
3. Choose an Android-native integration path (NDK + CMake, with a minimal Java/Kotlin activity only where required).
4. Port the platform layer: window/surface lifecycle, touch input, orientation, audio, storage, and asset discovery.
5. Enable and build the client targets for Android ABIs; resolve compiler and platform incompatibilities.
6. Add touch controls and phone-sized UI, then test on a real Android device.
7. Package only code and assets that may lawfully be redistributed. Original Valve game data must be supplied separately by the user where required.

## Current status

- [x] Create an isolated working branch.
- [x] Perform initial build/license audit.
- [ ] Android app scaffold.
- [ ] NDK/CMake cross-compilation.
- [ ] Client and platform-layer port.
- [ ] Touch controls and mobile UI.
- [ ] Device build and runtime verification.

No APK has been built, and no playable Android port is claimed at this stage.
