package net.plzpoint.kgmaster

import android.content.Context
import android.text.Layout
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

/**
 * Created by junsu on 2017-01-01.
 */

class CommentData {
    var id = ""
    var comment = ""
    var date = ""

    constructor() {

    }

    constructor(id: String, comment: String, date: String) {
        this.id = id
        this.comment = comment
        this.date = date
    }
}

class CommentHolder(view: View?) {
    var id: TextView? = null
    var comment: TextView? = null
    var date: TextView? = null

    init {
        id = view!!.findViewById(R.id.kg_comment_id) as TextView
        date = view!!.findViewById(R.id.kg_comment_date) as TextView
        comment = view!!.findViewById(R.id.kg_comment_comment) as TextView
    }
}

class CommentAdapter(context: Context) : BaseAdapter() {
    val inflater: LayoutInflater?
    val comments = ArrayList<CommentData>()

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return comments.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return comments[position]
    }

    fun addComment(comment: CommentData) {
        comments.add(comment)
    }

    fun reverse() {
        comments.reverse()
    }

    fun clear() {
        comments.clear()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val holder: CommentHolder?
        if (convertView == null) {
            view = inflater!!.inflate(R.layout.kg_comment_contents, parent, false)
            holder = CommentHolder(view)
            view!!.tag = holder
        } else {
            view = convertView
            holder = view!!.tag as CommentHolder
        }

        holder!!.id!!.text = comments[position].id
        holder!!.comment!!.text = comments[position].comment
        holder!!.date!!.text = comments[position].date
        return view
    }
}