package org.opengoldsrc.android

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager

class MainActivity : Activity(), SurfaceHolder.Callback {
    private lateinit var gameSurface: SurfaceView

    external fun nativeSetSurface(surface: Surface?)
    external fun nativeOnTouch(action: Int, pointerId: Int, x: Float, y: Float, width: Int, height: Int)

    companion object {
        init {
            System.loadLibrary("ogs_android")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        gameSurface = object : SurfaceView(this) {
            override fun onTouchEvent(event: MotionEvent): Boolean {
                val index = event.actionIndex.coerceIn(0, event.pointerCount - 1)
                nativeOnTouch(
                    event.actionMasked,
                    event.getPointerId(index),
                    event.getX(index),
                    event.getY(index),
                    width,
                    height
                )
                return true
            }
        }
        gameSurface.holder.addCallback(this)
        gameSurface.isFocusable = true
        gameSurface.isFocusableInTouchMode = true
        setContentView(gameSurface)
        enterImmersiveMode()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        nativeSetSurface(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        nativeSetSurface(holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        nativeSetSurface(null)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersiveMode()
    }

    private fun enterImmersiveMode() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
}
