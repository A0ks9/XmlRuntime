package com.flipkart.android.proteus.view.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Shader
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatRatingBar

/**
 * A custom {@link AppCompatRatingBar} that allows fixing the width based on a sample tile bitmap.
 *
 * `FixedRatingBar` extends the standard Android {@link RatingBar} and provides a way to control
 * its width based on the width of a provided {@link Bitmap} tile used for the stars and the number of stars.
 * This is useful when you need to ensure a consistent width for the RatingBar regardless of star image size,
 * especially if the star images (tiles) have a specific width and you want the RatingBar to precisely
 * accommodate a certain number of these tiles horizontally.
 *
 * **Key Features:**
 *
 * *   **Width Adjustment based on Tile Bitmap:**  Allows setting a {@link Bitmap} (`sampleTile`) and dynamically adjusts
 *     the RatingBar's width in {@link #onMeasure(int, int)} based on the `sampleTile`'s width and the number of stars.
 * *   **Tiled Drawable Support:** Provides {@link #getTiledDrawable(Drawable, boolean)} method, taken from AOSP,
 *     to convert a given {@link Drawable} into a tiled version of itself, handling {@link LayerDrawable} and
 *     {@link BitmapDrawable} recursively. This is used to tile the star images within the RatingBar.
 * *   **Rounded Corner Shape:**  Uses a {@link RoundRectShape} for the tiles, providing slightly rounded corners to the star drawables via {@link #getDrawableShape()}.
 * *   **Constructors mirroring {@link RatingBar}:** Includes constructors to be used in different Android inflation scenarios.
 *
 * **Usage Scenario:**
 *
 * Use `FixedRatingBar` when you want to have a RatingBar where:
 *
 * 1.  The width should be precisely calculated based on the size of the star images (tiles).
 * 2.  You want to use custom star images (bitmaps) and ensure they tile correctly within the RatingBar.
 * 3.  You want to have control over the shape of the star drawables (e.g., rounded corners provided by {@link #getDrawableShape()}).
 *
 * To use `FixedRatingBar`, you might:
 *
 * 1.  Create or load a {@link Bitmap} that you want to use as a sample tile for the stars.
 * 2.  Set this {@link Bitmap} to the `FixedRatingBar` using {@link #setSampleTile(Bitmap)}.
 * 3.  The RatingBar will then automatically adjust its width in {@link #onMeasure(int, int)} to accommodate
 *     the specified number of stars based on the `sampleTile`'s width.
 * 4.  You can further customize the star drawables by using the {@link #getTiledDrawable(Drawable, boolean)} method if needed.
 *
 * @see AppCompatRatingBar
 */
open class FixedRatingBar : AppCompatRatingBar {
    private var sampleTile: Bitmap? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    fun setSampleTile(bitmap: Bitmap?) {
        this.sampleTile = bitmap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        sampleTile?.let { // Safe call to execute block only if sampleTile is not null
            val width = it.width * numStars
            setMeasuredDimension(resolveSize(width, widthMeasureSpec), measuredHeight)
        }
    }

    private fun getDrawableShape(): Shape {
        val roundedCorners =
            floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f) // Kotlin needs floats (f suffix)
        return RoundRectShape(roundedCorners, null, null)
    }

    /**
     * Taken from AOSP !!
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    fun getTiledDrawable(drawable: Drawable, clip: Boolean): Drawable {

        return if (drawable is LayerDrawable) {
            val background = drawable
            val n = background.numberOfLayers
            val outDrawables =
                arrayOfNulls<Drawable>(n) // Kotlin needs arrayOfNulls for Drawable array

            for (i in 0 until n) {
                val id = background.getId(i)
                outDrawables[i] = getTiledDrawable(
                    background.getDrawable(i),
                    (id == android.R.id.progress || id == android.R.id.secondaryProgress)
                )
            }

            val newBg = LayerDrawable(
                outDrawables.filterNotNull().toTypedArray()
            ) // Filter nulls and convert to typed array

            for (i in 0 until n) {
                newBg.setId(i, background.getId(i))
            }
            newBg

        } else if (drawable is BitmapDrawable) {

            val tileBitmap = drawable.bitmap
            if (sampleTile == null) {
                sampleTile = tileBitmap
            }
            val shapeDrawable = ShapeDrawable(getDrawableShape())

            val bitmapShader = BitmapShader(
                tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP
            )
            shapeDrawable.paint.shader = bitmapShader

            if (clip) ClipDrawable(
                shapeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL
            ) else shapeDrawable
        } else {
            drawable
        }
    }
}