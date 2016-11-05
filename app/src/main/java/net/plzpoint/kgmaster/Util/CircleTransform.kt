package net.plzpoint.kgmaster.Util
import android.content.Context
import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

/**
 * Created by junsu on 2016-10-14.
 * 원형으로 자른 Bitmap을 리턴한다.
 *
 * 사용법: Glide 라이브러리 사용
 *         Glide.with(this).load("http://junsueg5737.dothome.co.kr/Resource/profile.png")
                            .crossFade()
                            .thumbnail(0.5f)
                            .bitmapTransform(CircleTransform(this@MainActivity))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(bubbleView!!.serviceHead)
 */

open class CircleTransform(context: Context) : BitmapTransformation(context) {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return CircleCrop(pool, toTransform)
    }

    private fun CircleCrop(pool: BitmapPool, source: Bitmap): Bitmap? {
        if (source == null)
            return null

        var size = Math.min(source.width, source.height)
        var x = (source.width - size)
        var y = (source.height - size)

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