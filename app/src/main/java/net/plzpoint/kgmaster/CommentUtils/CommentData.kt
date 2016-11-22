package net.plzpoint.kgmaster.CommentUtils

import android.graphics.Bitmap

/**
 * Created by junsu on 2016-11-22.
 */
// 이미지, 아이디(닉 네임), 시간, 댓글 내용
class CommentData(bitmap: Bitmap, id : String, time : String, comment : String)
{
    val bitmap : Bitmap
    val id : String
    val time : String
    val comment : String

    init {
        this.bitmap = bitmap
        this.id = id
        this.time = time
        this.comment = comment
    }
}