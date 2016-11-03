package net.plzpoint.kgmaster

import android.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

open class MealFragment : Fragment() {

    fun newInstance() : MealFragment {
        val mealFragment = MealFragment()
        return mealFragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.kg_meal,container,false)
    }
}
