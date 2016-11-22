package net.plzpoint.kgmaster.CommentUtils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import net.plzpoint.kgmaster.R
import java.util.*

/**
 * Created by junsu on 2016-11-22.
 */
class CommentAdapter(context: Context) : BaseAdapter()
{
    val context : Context
    val commentList : ArrayList<CommentData>
    val inflater: LayoutInflater

    init {
        this.context = context
        this.commentList = ArrayList<CommentData>()
        this.inflater = LayoutInflater.from(context)
    }

    fun addComment(commentItem : CommentData) : Boolean{
        return commentList!!.add(commentItem)
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }

    override fun getCount(): Int {
        return commentList.count()
    }

    override fun getItem(position: Int): Any {
        return commentList.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val commentholder : CommentViewHolder
        val view : View?

        if(convertView == null){
            view = inflater.inflate(R.layout.comment_box,parent,false)
            commentholder = CommentViewHolder(view)
            view!!.setTag(commentholder)
        }
        else
        {
            view = convertView
            commentholder = view!!.getTag() as CommentViewHolder
        }

        // commentholder.profile
        // commentholder.id
        // commentholder.time
        // commentholder.comment
        return view
    }
}