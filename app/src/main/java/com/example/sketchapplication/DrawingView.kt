package com.example.sketchapplication


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var drawPath: Path = Path()
    private var drawPaint: Paint = Paint()
    private var canvasPaint: Paint = Paint()
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    var paintColor: Int = Color.BLACK
    var brushThickness: Float = 10f
        set(value) {
            field = value
            drawPaint.strokeWidth = value
        }
    var eraserSize: Float = 100f
    private var eraserPaint: Paint = Paint()
    var isUsingEraser: Boolean = false
        set(value) {
            field = value
            if (value) {
                drawPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                drawPaint.xfermode = null
            }
        }

    var isRectangleDrawingMode: Boolean = false

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var currentX: Float = 0f
    private var currentY: Float = 0f

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint.color = paintColor
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = brushThickness
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND

        canvasPaint = Paint(Paint.DITHER_FLAG)

        eraserPaint.color = Color.WHITE
        eraserPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        if (isRectangleDrawingMode) {
            canvas.drawRect(startX, startY, currentX, currentY, drawPaint)
        } else {
            canvas.drawPath(drawPath, drawPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isRectangleDrawingMode) {
                    startX = touchX
                    startY = touchY
                } else {
                    if (isUsingEraser) {
                        drawCanvas?.drawRect(
                            touchX - eraserSize / 2,
                            touchY - eraserSize / 2,
                            touchX + eraserSize / 2,
                            touchY + eraserSize / 2,
                            eraserPaint
                        )
                    } else {
                        drawPath.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isRectangleDrawingMode) {
                    currentX = touchX
                    currentY = touchY
                } else {
                    if (isUsingEraser) {
                        drawCanvas?.drawRect(
                            touchX - eraserSize / 2,
                            touchY - eraserSize / 2,
                            touchX + eraserSize / 2,
                            touchY + eraserSize / 2,
                            eraserPaint
                        )
                    } else {
                        drawPath.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isRectangleDrawingMode) {
                    drawCanvas?.drawRect(startX, startY, currentX, currentY, drawPaint)
                } else {
                    drawCanvas?.drawPath(drawPath, drawPaint)
                    drawPath.reset()
                }
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun updateBrushSize(newSize: Float) {
        brushThickness = newSize
    }

    fun changeColor(newColor: Int) {
        paintColor = newColor
        drawPaint.color = paintColor
    }

    fun toggleEraserMode(isErase: Boolean) {
        isUsingEraser = isErase
    }

    fun toggleRectangleMode(isRectangle: Boolean) {
        isRectangleDrawingMode = isRectangle
    }

    fun updateEraserSize(size: Float) {
        eraserSize = size
    }
}


