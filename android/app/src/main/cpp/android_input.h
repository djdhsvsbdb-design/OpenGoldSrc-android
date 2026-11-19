#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef enum OGSAndroidInputEventType {
    OGS_ANDROID_INPUT_BUTTON = 1,
    OGS_ANDROID_INPUT_MOVE = 2,
    OGS_ANDROID_INPUT_LOOK = 3
} OGSAndroidInputEventType;

typedef struct OGSAndroidInputEvent {
    OGSAndroidInputEventType type;
    int control;
    int pressed;
    float x;
    float y;
} OGSAndroidInputEvent;

/*
 * Non-blocking event queue consumed by the future GoldSrc platform-input
 * adapter. Returns 1 when an event was written to out_event, otherwise 0.
 */
int OGS_Android_PollInputEvent(OGSAndroidInputEvent* out_event);

#ifdef __cplusplus
}
#endif
