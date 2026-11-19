# Android Port Status

## Initial repository audit

Audit date: 2026-11-19. Android work is isolated on `android-port-bootstrap`; `master` has not been modified.

- OpenGoldSrc is an engine recreation, not a complete Counter-Strike 1.6 distribution.
- The active CMake target list does not currently build the client engine/render/audio stack.
- The repository notes GPLv3 but also warns that some source derives from material not officially open-sourced by Valve. Review provenance and licenses before distributing an APK.

## Android scaffold

- Kotlin Activity, landscape/full-screen mode, Android Surface lifecycle bridge.
- NDK/CMake native library.
- ABI filters: `arm64-v8a`, `armeabi-v7a`, `x86_64`.
- Native surface and input JNI entry points.

## Mobile touch UI added

- Left virtual joystick for movement.
- Right-side drag area for relative view/look input.
- On-screen FIRE, JUMP, DUCK, RELOAD, USE, NEXT WEAPON, and SCORE controls.
- Multi-touch pointer tracking so movement/look/buttons can be used simultaneously.
- Controls scale with the shorter screen dimension for phone/tablet layouts.

The Kotlin UI now emits semantic native input events. The C++ bridge currently logs those events; it is **not yet connected to GoldSrc's actual input system**, so the controls are a UI/input scaffold, not proof of playable controls.

## Remaining work

1. Connect native control/look events to the GoldSrc client input implementation.
2. Integrate client engine, renderer, filesystem, audio, lifecycle, and game assets.
3. Add user-adjustable control positions, size, opacity, and sensitivity.
4. Build with Android SDK/NDK and test on physical phones/tablets across supported ABIs.
5. Package only code/assets that may lawfully be redistributed. Original Valve game data may need to be supplied separately.

No APK has been built or device-tested. No playable Android port is claimed.
