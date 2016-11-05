package net.plzpoint.kgmaster.Fragment

import net.plzpoint.kgmaster.R
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by junsu on 2016-11-05.
 */
class NewsFragment : Fragment() {

    fun newInitialize(): NewsFragment {
        val newFragment = NewsFragment()
        return newFragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.kg_news, container, false)
    }
}