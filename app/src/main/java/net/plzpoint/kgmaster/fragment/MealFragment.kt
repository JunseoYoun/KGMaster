package net.plzpoint.kgmaster.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import com.androidquery.AQuery
import net.plzpoint.kgmaster.R
import net.plzpoint.kgmaster.activity.MainActivity
import net.plzpoint.kgmaster.utils.MealManager
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

        val good: TextView
        val bad: TextView

        init {
            this.day = view.findViewById(R.id.kg_meal_day) as TextView
            this.data0 = view.findViewById(R.id.kg_meal_data0) as TextView
            this.data1 = view.findViewById(R.id.kg_meal_data1) as TextView
            this.data2 = view.findViewById(R.id.kg_meal_data2) as TextView
            this.data3 = view.findViewById(R.id.kg_meal_data3) as TextView
            this.data4 = view.findViewById(R.id.kg_meal_data4) as TextView
            this.data5 = view.findViewById(R.id.kg_meal_data5) as TextView
            this.choice = view.findViewById(R.id.kg_meal_choice_bar) as SeekBar

            this.good = view.findViewById(R.id.kg_meal_choice_good) as TextView
            this.bad = view.findViewById(R.id.kg_meal_choice_bad) as TextView
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
            holder.choice.max = meals[position].badChoice + meals[position].goodChoice
            holder.choice.progress = meals[position].goodChoice
            holder.good.setOnClickListener(meals[position].goodCallback)
            holder.bad.setOnClickListener(meals[position].badCallback)
            return view
        }
    }

    var aq: AQuery? = null
    var mDay = 0
    var mMealListView: ListView? = null
    var mMealListViewAdapter: MealAdapter? = null
    var mealManager: MealManager? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Initialize
        val mInflater = inflater!!.inflate(R.layout.kg_meal_fragment, container, false)
        val d = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val today = sdf.format(d)

        val oCalendar = Calendar.getInstance()
        val dayOfWeek = oCalendar.get(Calendar.DAY_OF_WEEK) - 1

        mMealListView = mInflater.findViewById(R.id.kg_meal_contents) as ListView
        mMealListViewAdapter = MealAdapter(activity.applicationContext)
        mMealListView!!.adapter = mMealListViewAdapter
        mDay = dayOfWeek
        aq = AQuery(activity.applicationContext)
        mealManager = MealManager(activity.applicationContext)

        Log.i("Date", today)

        // Get Meals
        mMealListViewAdapter!!.meals.clear()
        mealManager!!.getMeal(mDay) { md, time ->
            // Good
            md.goodCallback = object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    mealManager!!.setChoice(today, time, 1) { good, bad ->
                        SetMealChoice(md, good, bad)
                        mMealListViewAdapter!!.notifyDataSetChanged()
                    }
                }
            }
            // Bad
            md.badCallback = object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    mealManager!!.setChoice(today, time, 0) { good, bad ->
                        SetMealChoice(md, good, bad)
                        mMealListViewAdapter!!.notifyDataSetChanged()
                    }
                }
            }
            mealManager!!.getChoice(today, time, { good, bad ->
                SetMealChoice(md, good, bad)
                mMealListViewAdapter!!.notifyDataSetChanged()
            })
            mMealListViewAdapter!!.meals.add(md)
            mMealListViewAdapter!!.notifyDataSetChanged()
            MainActivity.Instance.instance!!.main_title!!.text = md.mealMonthDay
        }

        return mInflater
    }

    fun SetMealChoice(data: MealManager.MealData, good: Int, bad: Int) {
        data.goodChoice = good
        data.badChoice = bad
    }
}