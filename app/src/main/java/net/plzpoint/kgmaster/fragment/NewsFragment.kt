package net.plzpoint.kgmaster.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.plzpoint.kgmaster.activity.MainActivity
import net.plzpoint.kgmaster.R
import java.util.*

/**
 * Created by junsu on 2017-01-18.
 */
class NewsFragment : Fragment() {

    fun instance(): NewsFragment {
        val fragment = NewsFragment()
        return fragment
    }

    class NewsData(title: String, url: String) {
        val title: String
        val url: String

        init {
            this.title = title
            this.url = url
        }
    }

    class NewsHolder(view: View?) {
        val title: TextView

        init {
            this.title = view!!.findViewById(R.id.kg_news_title) as TextView
        }
    }

    class NewsAdapter(context: Context) : BaseAdapter() {
        val news = ArrayList<NewsData>()
        val inflater: LayoutInflater

        override fun getCount(): Int {
            return news.size
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getItem(p0: Int): Any {
            return news[p0]
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val view: View
            var holder: NewsHolder

            if (p1 == null) {
                view = inflater.inflate(R.layout.kg_news_contents, p2, false)
                holder = NewsHolder(view)
                view.tag = holder
            } else {
                view = p1
                holder = view.tag as NewsHolder
            }

            holder.title.text = news[p0].title

            return view
        }

        init {
            inflater = LayoutInflater.from(context)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mInflater = inflater!!.inflate(R.layout.kg_news_fragment, container, false)

        MainActivity.Instance.instance!!.main_title!!.text = "뉴스"
        MainActivity.Instance.instance!!.kg_button_layout!!.visibility = View.INVISIBLE
        return mInflater
    }
}