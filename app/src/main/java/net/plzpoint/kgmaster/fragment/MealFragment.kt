package net.plzpoint.kgmaster.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    var mMealListView: ListView? = null
    var mMealListViewAdapter: MealAdapter? = null
    var mealManager: MealManager? = null
    var mealProgress: ProgressBar? = null

    var dayNum = 0
    // 2017-04
    var year = ""
    var month = ""
    var day = ""

    // 2017-04-15
    var masterDay = ""
    var simpleDateFormat = SimpleDateFormat("yyyy-MM")
    var calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Initialize
        val mInflater = inflater!!.inflate(R.layout.kg_meal_fragment, container, false)

        mMealListView = mInflater.findViewById(R.id.kg_meal_contents) as ListView
        mMealListViewAdapter = MealAdapter(activity.applicationContext)
        mMealListView!!.adapter = mMealListViewAdapter
        aq = AQuery(activity.applicationContext)
        mealManager = MealManager(activity.applicationContext)
        mealProgress = mInflater!!.findViewById(R.id.kg_meal_progress) as ProgressBar

        todayCal()
        getMeal()

        mealProgress!!.visibility = View.VISIBLE
        mMealListView!!.visibility = View.INVISIBLE

        return mInflater
    }

    fun todayCal() {
        simpleDateFormat = SimpleDateFormat("yyyy")
        year = simpleDateFormat.format(Date())
        simpleDateFormat = SimpleDateFormat("MM")
        month = simpleDateFormat.format(Date())
        simpleDateFormat = SimpleDateFormat("dd")
        day = simpleDateFormat.format(Date())

        masterCal()
    }

    fun tomorrowCal() {
        val max = calendar.getMaximum(Calendar.DAY_OF_MONTH)
        var minDay = day.toInt()
        var minMonth = month.toInt()
        var minYear = year.toInt()

        minDay += 1
        if (max < minDay) {
            minDay = 1
            minMonth += 1
            if (12 < minMonth) {
                minMonth = 1
                minYear += 1
            }
        }
        day = minDay.toString()
        month = minMonth.toString()
        year = minYear.toString()

        masterCal()
    }

    fun yesterdayCal() {
        var minDay = day.toInt()
        var minMonth = month.toInt()
        var minYear = year.toInt()

        minDay -= 1
        if (minDay < 1) {
            minMonth -= 1
            if (minMonth < 1) {
                minMonth = 12
                minYear -= 1
            }
        }
        day = minDay.toString()
        month = minMonth.toString()
        year = minYear.toString()

        masterCal()
    }

    fun masterCal() {
        masterDay = year.plus("-").plus(month).plus("-").plus(day)
        Log.i("Master Day", masterDay)

        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        // 특정날자 요일
        val date = simpleDateFormat.parse(masterDay)
        calendar.time = date
        dayNum = calendar.get(Calendar.DAY_OF_WEEK) - 1
    }

    fun setMealChoice(data: MealManager.MealData, good: Int, bad: Int) {
        data.goodChoice = good
        data.badChoice = bad
    }

    fun getMeal() {
        // Get Meals
        mMealListViewAdapter!!.meals.clear()
        mealManager!!.getMeal(masterDay, dayNum) { md, time ->
            // Good
            md.goodCallback = object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    mealManager!!.setChoice(masterDay, time, 1) { good, bad ->
                        setMealChoice(md, good, bad)
                        mMealListViewAdapter!!.notifyDataSetChanged()
                    }
                }
            }
            // Bad
            md.badCallback = object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    mealManager!!.setChoice(masterDay, time, 0) { good, bad ->
                        setMealChoice(md, good, bad)
                        mMealListViewAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            mealManager!!.getChoice(masterDay, time, { good, bad ->
                setMealChoice(md, good, bad)
                mMealListViewAdapter!!.notifyDataSetChanged()
            })

            mealProgress!!.visibility = View.INVISIBLE
            mMealListView!!.visibility = View.VISIBLE
            mMealListViewAdapter!!.meals.add(md)
            mMealListViewAdapter!!.notifyDataSetChanged()
            MainActivity.Instance.instance!!.main_title!!.text = md.mealMonthDay
        }
    }
}