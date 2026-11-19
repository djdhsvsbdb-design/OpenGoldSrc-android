#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#include <jni.h>

#include <mutex>

namespace {
constexpr const char* kTag = "OpenGoldSrc";
std::mutex g_surface_mutex;
ANativeWindow* g_window = nullptr;
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
    // Placeholder until GoldSrc client input is connected.
    __android_log_print(ANDROID_LOG_VERBOSE, kTag,
        "control=%d pressed=%d x=%.2f y=%.2f",
        control, pressed ? 1 : 0, value_x, value_y);
}

extern "C" JNIEXPORT void JNICALL
Java_org_opengoldsrc_android_MainActivity_nativeOnLook(
    JNIEnv* /* env */, jobject /* thiz */, jfloat delta_x, jfloat delta_y) {
    // Relative look deltas are normalized; engine sensitivity is applied at integration.
    __android_log_print(ANDROID_LOG_VERBOSE, kTag,
        "look dx=%.3f dy=%.3f", delta_x, delta_y);
}
