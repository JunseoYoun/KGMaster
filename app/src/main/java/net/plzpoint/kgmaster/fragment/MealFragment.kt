package net.plzpoint.kgmaster.Fragment

import android.app.Fragment
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.plzpoint.kgmaster.R

open class MealFragment : Fragment(){

    fun newInstance() : MealFragment {
        val mealFragment = MealFragment()
        return mealFragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.kg_meal_contents,container,false)
    }
}