package net.plzpoint.kgmaster.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.plzpoint.kgmaster.activity.MainActivity
import net.plzpoint.kgmaster.R

/**
 * Created by junsu on 2017-01-18.
 */
class NewsFragment : Fragment() {

    fun instance(): NewsFragment {
        val fragment = NewsFragment()
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mInflater = inflater!!.inflate(R.layout.kg_news_fragment, container, false)

        MainActivity.Instance.instance!!.main_title!!.text = "뉴스"
        MainActivity.Instance.instance!!.kg_button_layout!!.visibility = View.INVISIBLE
        return mInflater
    }
}