package net.plzpoint.kgmaster.CommentUtils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.comment_box.*
import net.plzpoint.kgmaster.R
import org.w3c.dom.Text

/**
 * Created by junsu on 2016-11-22.
 */

class CommentViewHolder(view : View)
{
    val profile : ImageView
    val id : TextView
    val time : TextView
    val comment : TextView

    init {
        this.profile = view.findViewById(R.id.comment_profile) as ImageView
        this.id = view.findViewById(R.id.comment_nickname) as TextView
        this.time = view.findViewById(R.id.comment_time) as TextView
        this.comment = view.findViewById(R.id.comment_contents) as TextView
    }
}
