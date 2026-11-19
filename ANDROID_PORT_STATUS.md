# Android Port Status

## Initial repository audit

Audit date: 2026-11-19

The Android work is isolated on `android-port-bootstrap`; `master` has not been modified.

Findings from the repository's original `master` snapshot:

- The README describes OpenGoldSrc as a recreation of the GoldSrc engine, not a complete Counter-Strike 1.6 distribution.
- The top-level build uses CMake and adds `dep/` and `ogs/`.
- `ogs/CMakeLists.txt` enables server/utility components but comments out key client-facing modules such as `engine`, `filesystem`, `game`, `render`, `sound`, and `voice`.
- The README says build and installation instructions are TODO.
- The README identifies GPLv3 but also warns that parts of the source derive from material not officially open-sourced by Valve. Component provenance and licensing need review before distributing an APK.

## Android scaffold added

A first Android Studio project now exists under `android/`:

- Kotlin Activity with landscape orientation and immersive full-screen mode.
- Android Surface lifecycle bridged to a native C++ library using `ANativeWindow_fromSurface`.
- Basic multi-pointer touch event bridge.
- NDK/CMake native target.
- ABI filters: `arm64-v8a`, `armeabi-v7a`, and `x86_64` (the last is for emulators).

This is infrastructure only. The native library currently logs surface and touch events; it does not yet render or run the GoldSrc client.

## Why it is not playable yet

The source snapshot's active CMake target list does not build the client engine/render/audio stack. The Android shell cannot become a game until the actual client modules and their dependencies are located, enabled, and adapted to Android.

## Next engineering steps

1. Map the source tree and identify the real client, renderer, filesystem, audio, input, and UI modules.
2. Verify dependency availability and review per-component licenses and provenance.
3. Integrate the client engine into the Android NDK build, selecting Android-compatible renderer and filesystem backends.
4. Port lifecycle, audio, storage, asset discovery, and input.
5. Add configurable touch controls and phone/tablet layouts.
6. Build and test on physical devices across supported Android ABIs.
7. Package only code and assets that may lawfully be redistributed. Original Valve game data must be supplied separately by the user where required.

## Current status

- [x] Isolated working branch created.
- [x] Initial build/license audit recorded.
- [x] Android application and native bridge scaffold added.
- [ ] GoldSrc client integration.
- [ ] Android renderer/audio/filesystem backends.
- [ ] Touch controls and mobile UI.
- [ ] NDK build and APK packaging.
- [ ] Device runtime verification.

No APK has been built. No playable Android port is claimed yet.
