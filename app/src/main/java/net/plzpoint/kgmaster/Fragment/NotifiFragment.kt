package net.plzpoint.kgmaster.Fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.plzpoint.kgmaster.R

/**
 * Created by junsu on 2016-11-05.
 */
class NotifiFragment : Fragment() {

    fun newInitialize(): NotifiFragment {
        val notifiFragment = NotifiFragment()
        return notifiFragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.kg_notifi, container, false)
    }
}