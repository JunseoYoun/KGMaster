package net.plzpoint.kgmaster

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

        return mInflater
    }
}