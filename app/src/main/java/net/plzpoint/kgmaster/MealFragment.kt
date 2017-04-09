package net.plzpoint.kgmaster

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import org.json.JSONObject
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class MealFragment : Fragment() {
    fun instance(): MealFragment {
        val fragment = MealFragment()
        return fragment
    }

    class MealHolder(view: View) {
        val day: TextView
        val data0: TextView
        val data1: TextView
        val data2: TextView
        val data3: TextView
        val data4: TextView
        val data5: TextView
        val choice: SeekBar

        init {
            this.day = view.findViewById(R.id.kg_meal_day) as TextView
            this.data0 = view.findViewById(R.id.kg_meal_data0) as TextView
            this.data1 = view.findViewById(R.id.kg_meal_data1) as TextView
            this.data2 = view.findViewById(R.id.kg_meal_data2) as TextView
            this.data3 = view.findViewById(R.id.kg_meal_data3) as TextView
            this.data4 = view.findViewById(R.id.kg_meal_data4) as TextView
            this.data5 = view.findViewById(R.id.kg_meal_data5) as TextView
            this.choice = view.findViewById(R.id.kg_meal_choice_bar) as SeekBar
        }
    }

    class MealAdapter(context: Context) : BaseAdapter() {
        val inflater: LayoutInflater
        val meals = ArrayList<MealManager.MealData>()

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return meals.count()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return meals[position]
        }

        fun clear() {
            meals.clear()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val holder: MealHolder?
            if (convertView == null) {
                view = inflater.inflate(R.layout.kg_meal_content, parent, false)
                holder = MealHolder(view)
                view.tag = holder
            } else {
                view = convertView
                holder = view.tag as MealHolder
            }

            holder.day.text = meals[position].day
            holder.data0.text = meals[position].data0
            holder.data1.text = meals[position].data1
            holder.data2.text = meals[position].data2
            holder.data3.text = meals[position].data3
            holder.data4.text = meals[position].data4
            holder.data5.text = meals[position].data5
            return view
        }
    }

    var aq: AQuery? = null
    var mDay = 0
    var mMealDay = 0
    var mMealListView: ListView? = null
    var mMealListViewAdapter: MealAdapter? = null
    val mealManager: MealManager

    init {
        mealManager = MealManager()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Initialize
        val mInflater = inflater!!.inflate(R.layout.kg_meal_fragment, container, false)
        val oCalendar = Calendar.getInstance()
        val dayOfWeek = oCalendar.get(Calendar.DAY_OF_WEEK) - 1
        mMealListView = mInflater.findViewById(R.id.kg_meal_contents) as ListView
        mMealListViewAdapter = MealAdapter(activity.applicationContext)
        mMealListView!!.adapter = mMealListViewAdapter
        mDay = dayOfWeek
        mMealDay = 0
        aq = AQuery(activity.applicationContext)

        // Get Meals
        mMealListViewAdapter!!.meals.clear()
        mealManager.getMeal(mDay) {
            mMealListViewAdapter!!.meals.add(it)
            mMealListViewAdapter!!.notifyDataSetChanged()
        }

        // Get Choice
        mealManager.getChoice { good, bad ->
            Log.i("Meal Choice", "Good : " + good.toString() + " " + bad.toString())
        }

        return mInflater
    }
}