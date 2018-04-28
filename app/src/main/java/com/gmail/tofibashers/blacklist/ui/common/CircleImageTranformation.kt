package com.gmail.tofibashers.blacklist.ui.common

import android.graphics.*
import com.squareup.picasso.Transformation


/**
 * Created by TofiBashers on 25.04.2018.
 */
class CircleImageTranformation : Transformation {

    override fun key(): String = key

    override fun transform(source: Bitmap): Bitmap {
        val resSize = Math.min(source.width, source.height)

        val xFirst = (source.width - resSize) / 2
        val yFirst = (source.height - resSize) / 2

        val squaredBitmap = Bitmap.createBitmap(source, xFirst, yFirst, resSize, resSize)
        if (squaredBitmap !== source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(resSize, resSize, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true
        val radius = resSize / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        squaredBitmap.recycle()
        return bitmap
    }

    companion object {
        const val key = "CIRCLE"
    }
}