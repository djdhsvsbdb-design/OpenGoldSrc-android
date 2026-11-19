#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#include <jni.h>

#include <deque>
#include <mutex>

#include "android_input.h"

namespace {
constexpr const char* kTag = "OpenGoldSrc";
constexpr std::size_t kMaxQueuedEvents = 256;
std::mutex g_surface_mutex;
ANativeWindow* g_window = nullptr;
std::mutex g_input_mutex;
std::deque<OGSAndroidInputEvent> g_input_events;

void QueueInputEvent(const OGSAndroidInputEvent& event) {
    std::lock_guard<std::mutex> lock(g_input_mutex);
    if (g_input_events.size() >= kMaxQueuedEvents) {
        g_input_events.pop_front();
    }
    g_input_events.push_back(event);
}
}

extern "C" JNIEXPORT void JNICALL
Java_org_opengoldsrc_android_MainActivity_nativeSetSurface(
    JNIEnv* env, jobject /* thiz */, jobject surface) {
    ANativeWindow* next = surface ? ANativeWindow_fromSurface(env, surface) : nullptr;
    std::lock_guard<std::mutex> lock(g_surface_mutex);
    if (g_window) ANativeWindow_release(g_window);
    g_window = next;
    __android_log_print(ANDROID_LOG_INFO, kTag, "Android surface %s",
                        g_window ? "attached" : "detached");
}

extern "C" JNIEXPORT void JNICALL
Java_org_opengoldsrc_android_MainActivity_nativeOnControl(
    JNIEnv* /* env */, jobject /* thiz */, jint control, jboolean pressed,
    jfloat value_x, jfloat value_y) {
    OGSAndroidInputEvent event{};
    event.type = (control == 0) ? OGS_ANDROID_INPUT_MOVE : OGS_ANDROID_INPUT_BUTTON;
    event.control = control;
    event.pressed = pressed ? 1 : 0;
    event.x = value_x;
    event.y = value_y;
    QueueInputEvent(event);
}

extern "C" JNIEXPORT void JNICALL
Java_org_opengoldsrc_android_MainActivity_nativeOnLook(
    JNIEnv* /* env */, jobject /* thiz */, jfloat delta_x, jfloat delta_y) {
    OGSAndroidInputEvent event{};
    event.type = OGS_ANDROID_INPUT_LOOK;
    event.x = delta_x;
    event.y = delta_y;
    QueueInputEvent(event);
}

extern "C" int OGS_Android_PollInputEvent(OGSAndroidInputEvent* out_event) {
    if (out_event == nullptr) return 0;
    std::lock_guard<std::mutex> lock(g_input_mutex);
    if (g_input_events.empty()) return 0;
    *out_event = g_input_events.front();
    g_input_events.pop_front();
    return 1;
}
