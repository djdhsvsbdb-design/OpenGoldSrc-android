# Android Port Status

## Repository findings

Android work is isolated on `android-port-bootstrap`; `master` has not been modified.

- OpenGoldSrc describes itself as a GoldSrc engine recreation, not a complete CS 1.6 distribution.
- The active `ogs/CMakeLists.txt` disables the client-facing engine, filesystem, game, renderer, sound, and voice modules.
- `ogs/engine/CMakeLists.txt` references implementation directories that are not present at the expected paths in this repository snapshot.
- The launcher is a desktop executable and expects dynamically loaded engine/filesystem modules.
- The README explicitly warns about code provenance and Valve licensing. Redistribution needs legal review.

## Android work completed on this branch

- Kotlin Activity with landscape/full-screen mode and Android Surface lifecycle bridge.
- NDK/CMake native shared library.
- ABI filters: `arm64-v8a`, `armeabi-v7a`, `x86_64`.
- Multi-touch overlay: movement joystick, look-drag region, FIRE, JUMP, DUCK, RELOAD, USE, NEXT WEAPON, SCORE.
- Control layout scales with the shorter screen dimension.
- JNI now places semantic control, movement-axis, and relative-look events into a bounded, thread-safe native queue.
- Added `android_input.h` with `OGS_Android_PollInputEvent`, the non-blocking interface intended for a future engine platform-input adapter.

## Critical integration gap

The native queue is an input transport layer only. There is no existing client input implementation available at the expected engine paths, and the engine/client modules are disabled in the current CMake configuration. No GoldSrc code currently polls the queue. Therefore the on-screen controls are not yet driving a playable game.

## Next steps

1. Locate or restore a complete, license-reviewed GoldSrc client source tree.
2. Implement an Android platform-input adapter that drains `OGS_Android_PollInputEvent` and maps events to the client's key/button/mouse interfaces.
3. Integrate Android-compatible renderer, filesystem, audio, app lifecycle, and storage.
4. Add editable control positions, size, opacity, and sensitivity.
5. Build with Android SDK/NDK and test on physical phones and tablets across supported ABIs.
6. Package only code and assets that may lawfully be redistributed. Original Valve game data may need to be supplied separately.

## Verification

No Android SDK/NDK build, APK packaging, or physical-device test has been run. No playable port is claimed.

## Source completeness check (2026-11-19)

A second pass checked the CMake dependency graph and expected source locations:

- The root build references `hlsdk-core`, `goldsrc-sdk`, and `shiftutil`, but these directories/files are absent from this repository snapshot; `.gitmodules` is empty.
- `ogs/CMakeLists.txt` has `engine`, `filesystem`, `game`, `render`, `sound`, and `voice` disabled.
- `ogs/engine/CMakeLists.txt` selects `Null`, `AC96`, or `RW17`, but the referenced implementation CMake files are absent.
- The `ogs/render/null` and `ogs/render/gl` CMake files are empty; the expected filesystem Stdio source file `FileSystemImpl.cpp` was not found in the snapshot.
- GitHub code search for client input entry points (including `IN_ActivateMouse`, `IN_Move`, `Key_Event`, and `IN_MouseEvent`) returned no matches in the repository's indexed default branch.

This is a source-tree/dependency gap, not just a missing Android build flag. Enabling the disabled CMake modules would not produce a client and would likely fail configuration or compilation. I have not fabricated a stub client or copied third-party engine code into the project: doing so would create a misleading, non-playable port and introduce unreviewed licensing obligations.

### Required decision for the next implementation stage

The project needs a complete, license-reviewed client source base. The practical routes are:

1. Restore the exact missing OpenGoldSrc dependencies/source revisions and then implement Android platform backends; or
2. Port an existing Android-capable GoldSrc-compatible client (for example, Xash3D FWGS) as a clearly separate engine integration, after reviewing its license and the user's intended project direction.

Until one of these source bases is selected and available, the native touch-event queue cannot be connected to real in-game controls.
