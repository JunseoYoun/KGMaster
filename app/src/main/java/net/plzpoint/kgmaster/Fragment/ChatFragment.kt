package net.plzpoint.kgmaster.Fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.plzpoint.kgmaster.R

/**
 * Created by junsu on 2016-11-03.
 */
open class ChatFragment : Fragment(){

    fun newInstance() : ChatFragment {
        val chatFragment = ChatFragment()
        return chatFragment
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.kg_chat,container,false)
    }
}