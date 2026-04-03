package com.thrillathon.client.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.facemesh.FaceMesh
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin
import androidx.core.graphics.withClip

class FaceOverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // 🎨 Paints
    private val ovalPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 9f
        isAntiAlias = true
    }

    private val dimPaint = Paint().apply {
        color = 0x88000000.toInt()
    }

    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = 0xFF00FF00.toInt()
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val pointPaint = Paint().apply {
        color = 0xFF00FF00.toInt()
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val scanPaint = Paint().apply {
        color = 0xFF00FF00.toInt()
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val arrowPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    // 📦 Data
    private val ovalRect = RectF()
    private var meshPoints: List<List<PointF>> = emptyList()
    private var meshBounds: List<RectF> = emptyList()

    private var progress = 0f
    private var isFaceInside = false
    private var isAnimating = false
    private var hasTriggered = false

    // 🔒 Stability
    private var lastCenterX = 0f
    private var lastCenterY = 0f
    private var stableFrames = 0
    private val STABLE_THRESHOLD = 2

    // 🔄 Animations
    private var scanX = 0f
    private var scanDirection = 1
    private var time = 0f

    var onProgressComplete: (() -> Unit)? = null

    // =========================
    // 🔥 INPUT
    // =========================
    fun setFaceMesh(
        meshes: List<FaceMesh>,
        imageWidth: Int,
        imageHeight: Int,
        rotation: Int,
        isFrontCamera: Boolean
    ) {
        if (width == 0 || height == 0) return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val imgW = if (rotation == 90 || rotation == 270) imageHeight.toFloat() else imageWidth.toFloat()
        val imgH = if (rotation == 90 || rotation == 270) imageWidth.toFloat() else imageHeight.toFloat()

        val scale = max(viewWidth / imgW, viewHeight / imgH)

        val offsetX = (imgW * scale - viewWidth) / 2f
        val offsetY = (imgH * scale - viewHeight) / 2f

        meshPoints = meshes.map { mesh ->
            mesh.allPoints.map { point ->
                var x = point.position.x * scale - offsetX
                var y = point.position.y * scale - offsetY
                if (isFrontCamera) x = viewWidth - x
                PointF(x, y)
            }
        }

        meshBounds = meshPoints.map {
            RectF(
                it.minOf { p -> p.x },
                it.minOf { p -> p.y },
                it.maxOf { p -> p.x },
                it.maxOf { p -> p.y }
            )
        }

        val face = meshBounds.firstOrNull()

        isFaceInside = face != null &&
                ovalRect.contains(face.centerX(), face.centerY()) &&
                isFaceCentered(face) &&
                isFaceStable(face)

        if (isFaceInside) startProgress()
        else resetProgress()

        invalidate()
    }

    // =========================
    // 🎯 CHECKS
    // =========================
    private fun isFaceCentered(rect: RectF): Boolean {
        val cx = width / 2f
        val cy = height / 2f
        return abs(rect.centerX() - cx) < 80 &&
                abs(rect.centerY() - cy) < 80
    }

    private fun isFaceStable(rect: RectF): Boolean {
        val dx = abs(rect.centerX() - lastCenterX)
        val dy = abs(rect.centerY() - lastCenterY)

        lastCenterX = rect.centerX()
        lastCenterY = rect.centerY()

        return if (dx < 10 && dy < 10) {
            stableFrames++
            stableFrames > STABLE_THRESHOLD
        } else {
            stableFrames = 0
            false
        }
    }

    // =========================
    // 🔄 PROGRESS (SMOOTH)
    // =========================
    private fun startProgress() {
        if (isAnimating) return
        isAnimating = true

        post(object : Runnable {
            override fun run() {

                if (!isFaceInside) {
                    resetProgress()
                    return
                }

                // Fast linear fill — completes in ~400ms at 16ms/frame
                progress += 15f

                if (progress >= 359f) {
                    progress = 360f

                    if (!hasTriggered) {
                        hasTriggered = true
                        onProgressComplete?.invoke()
                    }

                    isAnimating = false
                    invalidate()
                    return
                }

                invalidate()
                postDelayed(this, 16)
            }
        })
    }

    fun resetProgress() {
        progress = 0f
        hasTriggered = false
        isAnimating = false
        invalidate()
    }

    // =========================
    // 🎨 DRAW
    // =========================
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        val ow = w * 0.7f
        val oh = h * 0.5f
        val left = (w - ow) / 2f
        val top = (h - oh) / 2f

        ovalRect.set(left, top, left + ow, top + oh)

        ovalPaint.color = if (isFaceInside) Color.GREEN else Color.WHITE

        // 🌑 Background
        val save = canvas.saveLayer(null, null)
        canvas.drawRect(0f, 0f, w, h, dimPaint)

        val clearPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        canvas.drawOval(ovalRect, clearPaint)
        canvas.restoreToCount(save)

        canvas.drawOval(ovalRect, ovalPaint)

        // 🟢 Progress
        if (isFaceInside) {
            canvas.drawArc(ovalRect, -90f, progress, false, progressPaint)
        }

        // 🔥 Scan line
        scanX += 6f * scanDirection
        if (scanX > ovalRect.right || scanX < ovalRect.left) scanDirection *= -1

        canvas.withClip(ovalRect) {
            drawLine(scanX, ovalRect.top, scanX, ovalRect.bottom, scanPaint)
        }

        // 🟢 Mesh
        meshPoints.flatten().forEach {
            canvas.drawCircle(it.x, it.y, 2f, pointPaint)
        }

        // 🔥 Smooth arrow animation
        time += 0.12f
        val arrowOffset = (sin(time.toDouble()) * 40).toFloat()

        drawArrows(canvas, arrowOffset)
    }

    // =========================
    // ➡️ ARROWS
    // =========================
    private fun drawArrows(canvas: Canvas, offset: Float) {
        val face = meshBounds.firstOrNull() ?: return

        val dx = face.centerX() - width / 2f
        val dy = face.centerY() - height / 2f

        val size = 90f

        if (dx > 80) drawArrow(canvas, ovalRect.centerX() - 120 - offset, ovalRect.centerY(), "LEFT", size)
        if (dx < -80) drawArrow(canvas, ovalRect.centerX() + 120 + offset, ovalRect.centerY(), "RIGHT", size)
        if (dy > 80) drawArrow(canvas, ovalRect.centerX(), ovalRect.centerY() - 120 - offset, "UP", size)
        if (dy < -80) drawArrow(canvas, ovalRect.centerX(), ovalRect.centerY() + 120 + offset, "DOWN", size)
    }

    private fun drawArrow(canvas: Canvas, cx: Float, cy: Float, dir: String, size: Float) {
        val tail = size * 2f
        val head = size * 0.5f
        val path = Path()

        when (dir) {
            "LEFT" -> {
                canvas.drawLine(cx + tail, cy, cx, cy, arrowPaint)
                path.moveTo(cx, cy)
                path.lineTo(cx + head, cy - head)
                path.lineTo(cx + head, cy + head)
            }
            "RIGHT" -> {
                canvas.drawLine(cx - tail, cy, cx, cy, arrowPaint)
                path.moveTo(cx, cy)
                path.lineTo(cx - head, cy - head)
                path.lineTo(cx - head, cy + head)
            }
            "UP" -> {
                canvas.drawLine(cx, cy + tail, cx, cy, arrowPaint)
                path.moveTo(cx, cy)
                path.lineTo(cx - head, cy + head)
                path.lineTo(cx + head, cy + head)
            }
            "DOWN" -> {
                canvas.drawLine(cx, cy - tail, cx, cy, arrowPaint)
                path.moveTo(cx, cy)
                path.lineTo(cx - head, cy - head)
                path.lineTo(cx + head, cy - head)
            }
        }

        path.close()
        canvas.drawPath(path, arrowPaint)
    }
}