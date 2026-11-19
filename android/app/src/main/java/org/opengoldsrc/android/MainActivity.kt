package org.opengoldsrc.android

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import kotlin.math.hypot
import kotlin.math.min

class MainActivity : Activity(), SurfaceHolder.Callback {
    private lateinit var gameSurface: SurfaceView

    external fun nativeSetSurface(surface: Surface?)
    external fun nativeOnControl(control: Int, pressed: Boolean, valueX: Float, valueY: Float)
    external fun nativeOnLook(deltaX: Float, deltaY: Float)

    companion object {
        const val CTRL_MOVE = 0
        const val CTRL_FIRE = 1
        const val CTRL_JUMP = 2
        const val CTRL_CROUCH = 3
        const val CTRL_RELOAD = 4
        const val CTRL_USE = 5
        const val CTRL_NEXT_WEAPON = 6
        const val CTRL_SCORE = 7
        init { System.loadLibrary("ogs_android") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val root = FrameLayout(this)
        gameSurface = SurfaceView(this).apply {
            holder.addCallback(this@MainActivity)
            isFocusable = true
            isFocusableInTouchMode = true
        }
        root.addView(gameSurface, FrameLayout.LayoutParams(-1, -1))
        root.addView(MobileControlsView(this), FrameLayout.LayoutParams(-1, -1))
        setContentView(root)
        enterImmersiveMode()
    }

    override fun surfaceCreated(holder: SurfaceHolder) { nativeSetSurface(holder.surface) }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        nativeSetSurface(holder.surface)
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) { nativeSetSurface(null) }

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

    private inner class MobileControlsView(context: Context) : View(context) {
        private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = dp(2f)
        }
        private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        }
        private val activePointers = mutableMapOf<Int, Int>()
        private var movePointer = -1
        private var lookPointer = -1
        private var stickX = 0f
        private var stickY = 0f
        private var lookX = 0f
        private var lookY = 0f
        private var opacity = 0.58f

        private data class Button(val id: Int, val title: String, val x: Float, val y: Float, val r: Float)

        private fun dp(v: Float) = v * resources.displayMetrics.density
        private fun base() = min(width.toFloat(), height.toFloat())
        private fun joyRadius() = base() * 0.145f
        private fun buttons(): List<Button> {
            val s = base()
            val r = s * 0.061f
            val right = width - s * 0.10f
            val bottom = height - s * 0.12f
            return listOf(
                Button(CTRL_FIRE, "FIRE", right, bottom - r * 1.45f, r * 1.12f),
                Button(CTRL_JUMP, "JUMP", right - r * 1.9f, bottom - r * 0.15f, r),
                Button(CTRL_CROUCH, "DUCK", right + r * 0.05f, bottom + r * 0.95f, r * 0.88f),
                Button(CTRL_RELOAD, "R", right - r * 2.0f, bottom - r * 2.15f, r * 0.76f),
                Button(CTRL_USE, "USE", right - r * 3.0f, bottom - r * 0.05f, r * 0.76f),
                Button(CTRL_NEXT_WEAPON, "NEXT", right - r * 3.0f, bottom - r * 1.75f, r * 0.76f),
                Button(CTRL_SCORE, "SCORE", width - r * 1.3f, s * 0.23f, r * 0.72f)
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val s = base()
            val jr = joyRadius()
            val jx = s * 0.20f
            val jy = height - s * 0.22f
            fill.color = Color.argb((opacity * 150).toInt(), 20, 24, 30)
            canvas.drawCircle(jx, jy, jr, fill)
            stroke.color = Color.argb(190, 235, 240, 245)
            canvas.drawCircle(jx, jy, jr, stroke)
            fill.color = Color.argb(190, 220, 230, 240)
            canvas.drawCircle(stickX.takeIf { movePointer != -1 } ?: jx,
                stickY.takeIf { movePointer != -1 } ?: jy, jr * 0.34f, fill)
            label.textSize = s * 0.035f
            label.color = Color.WHITE
            canvas.drawText("MOVE", jx, jy + jr + s * 0.045f, label)

            for (button in buttons()) {
                val pressed = activePointers.values.contains(button.id)
                fill.color = if (pressed) Color.argb(205, 48, 125, 190)
                    else Color.argb((opacity * 255).toInt(), 28, 35, 44)
                canvas.drawCircle(button.x, button.y, button.r, fill)
                stroke.color = Color.argb(210, 235, 240, 245)
                canvas.drawCircle(button.x, button.y, button.r, stroke)
                label.textSize = button.r * if (button.title.length > 4) 0.28f else 0.34f
                canvas.drawText(button.title, button.x, button.y + label.textSize * 0.35f, label)
            }
            label.textSize = s * 0.026f
            label.color = Color.argb(180, 255, 255, 255)
            canvas.drawText("DRAG RIGHT SIDE TO LOOK", width * 0.70f, height - s * 0.055f, label)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val idx = event.actionIndex.coerceIn(0, event.pointerCount - 1)
            val id = event.getPointerId(idx)
            val x = event.getX(idx)
            val y = event.getY(idx)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    val s = base()
                    if (x < width * 0.43f && y > height * 0.48f && movePointer == -1) {
                        movePointer = id
                        updateStick(x, y)
                    } else if (x > width * 0.43f && y > height * 0.30f && lookPointer == -1 &&
                        buttons().none { hypot(x - it.x, y - it.y) <= it.r }) {
                        lookPointer = id
                        lookX = x
                        lookY = y
                    } else {
                        val button = buttons().firstOrNull { hypot(x - it.x, y - it.y) <= it.r }
                        if (button != null) {
                            activePointers[id] = button.id
                            nativeOnControl(button.id, true, 0f, 0f)
                        }
                    }
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    val s = base().coerceAtLeast(1f)
                    for (p in 0 until event.pointerCount) {
                        val pid = event.getPointerId(p)
                        val px = event.getX(p)
                        val py = event.getY(p)
                        if (pid == movePointer) updateStick(px, py)
                        if (pid == lookPointer) {
                            val dx = px - lookX
                            val dy = py - lookY
                            nativeOnLook(dx / s.coerceAtLeast(1f), dy / s.coerceAtLeast(1f))
                            lookX = px
                            lookY = py
                        }
                    }
                    invalidate()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                    if (id == movePointer) {
                        movePointer = -1
                        nativeOnControl(CTRL_MOVE, false, 0f, 0f)
                    }
                    if (id == lookPointer) lookPointer = -1
                    activePointers.remove(id)?.let { nativeOnControl(it, false, 0f, 0f) }
                    invalidate()
                }
            }
            return true
        }

        private fun updateStick(x: Float, y: Float) {
            val s = base()
            val cx = s * 0.20f
            val cy = height - s * 0.22f
            val radius = joyRadius()
            val dx = x - cx
            val dy = y - cy
            val length = hypot(dx, dy).coerceAtLeast(1f)
            val scale = (radius / length).coerceAtMost(1f)
            stickX = cx + dx * scale
            stickY = cy + dy * scale
            nativeOnControl(CTRL_MOVE, true, ((stickX - cx) / radius).coerceIn(-1f, 1f),
                (-(stickY - cy) / radius).coerceIn(-1f, 1f))
        }
    }

}
