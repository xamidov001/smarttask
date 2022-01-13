package uz.pdp.smarttask.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomImageView : View {

    lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private val paint = Paint()
    private val presenter = FloodFill()
    private var isClick = false
    private var isClear = false


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context): super(context)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bitmap, 0f, 0f, Paint())

        if (isClear) {
            canvas.drawColor(Color.CYAN)
            isClear = true
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(Color.CYAN)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)

    }

    fun setRandom(it: Bitmap) {
        bitmap = it
        canvas.drawBitmap(bitmap, 0f, 0f, Paint())
    }

    fun cleaner() {
        isClear = true
        invalidate()
    }


}