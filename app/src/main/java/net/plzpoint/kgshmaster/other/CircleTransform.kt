package net.plzpoint.kgshmaster.other

import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

/**
 * Created by junsu on 2016-10-03.
 */

open class CircleTransform(context: Context) : BitmapTransformation(context) {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap): Bitmap? {
        if (source == null)
            return null

        var size = Math.min(source.width, source.height);
        var x = (source.width - size);
        var y = (source.height - size);

        // TODO : this could be acquired from the pool too
        var squared = Bitmap.createBitmap(source, x, y, size, size)

        var result = pool.get(size, size, Bitmap.Config.ARGB_8888)
        if (result == null)
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        var canvas = Canvas(result)
        var paint = Paint()

        paint.setShader(BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        paint.isAntiAlias = true;

        var r = size / 2.0f;

        canvas.drawCircle(r, r, r, paint)

        return result
    }

    override fun getId(): String {
        return javaClass.toString();
    }
}