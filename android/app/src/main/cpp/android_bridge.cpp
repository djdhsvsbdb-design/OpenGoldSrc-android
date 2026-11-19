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
    if (g_window) {
        ANativeWindow_release(g_window);
    }
    g_window = next;

    if (g_window) {
        __android_log_print(ANDROID_LOG_INFO, kTag, "Android surface attached");
    } else {
        __android_log_print(ANDROID_LOG_INFO, kTag, "Android surface detached");
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_opengoldsrc_android_MainActivity_nativeOnTouch(
    JNIEnv* /* env */, jobject /* thiz */, jint action, jint pointer_id,
    jfloat x, jfloat y, jint width, jint height) {
    // Touch events are bridged here; mapping to GoldSrc input is a later port step.
    __android_log_print(
        ANDROID_LOG_VERBOSE, kTag,
        "touch action=%d pointer=%d x=%.1f y=%.1f size=%dx%d",
        action, pointer_id, x, y, width, height);
}
