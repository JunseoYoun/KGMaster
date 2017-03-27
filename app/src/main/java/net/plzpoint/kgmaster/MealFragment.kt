package net.plzpoint.kgmaster

import android.app.Fragment
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import kotlinx.android.synthetic.main.contents_nav_header.*
import kotlinx.android.synthetic.main.kg_meal_fragment.*
import kotlinx.android.synthetic.main.kg_meal_fragment.view.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import android.view.View.OnTouchListener
import android.view.MotionEvent
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE

class MealFragment : Fragment() {
    fun instance(): MealFragment {
        val fragment = MealFragment()
        return fragment
    }

    class MealData {
        var day: String = ""
        var data0: String = ""
        var data1: String = ""
        var data2: String = ""
        var data3: String = ""
        var data4: String = ""
        var data5: String = ""

        constructor(day: String, data0: String, data1: String, data2: String, data3: String, data4: String, data5: String) {
            this.day = day
            this.data0 = data0
            this.data1 = data1
            this.data2 = data2
            this.data3 = data3
            this.data4 = data4
            this.data5 = data5
        }
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
        val meals = ArrayList<MealData>()

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

        fun add(meal_day: Int, meals: ArrayList<String>) {
            var day_string = "아침"
            if (meal_day == 3) {
                day_string = "점심"
            } else if (meal_day == 5) {
                day_string = "저녁"
            }
            this.meals.add(MealData(day_string, meals[0], meals[1], meals[2], meals[3], meals[4], meals[5]))
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mInflater = inflater!!.inflate(R.layout.kg_meal_fragment, container, false)
        val oCalendar = Calendar.getInstance()
        val dayOfWeek = oCalendar.get(Calendar.DAY_OF_WEEK) - 1

        mMealListView = mInflater.findViewById(R.id.kg_meal_contents) as ListView
        mMealListViewAdapter = MealAdapter(activity.applicationContext)
        mMealListView!!.adapter = mMealListViewAdapter

        mDay = dayOfWeek
        mMealDay = 0
        aq = AQuery(activity.applicationContext)
        // 오늘 날자 급식을 가져옴
        getMeals(mDay)
        // 오늘 날자 만족도를 가져옴
        mealChoice(1)
        return mInflater
    }

    // choice : good, bad, null
    fun mealChoice(onlyChoice: Int, choice: Int = 3) {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val hashMap = HashMap<String, Any>()

        hashMap.put("KG_CONTENTS_TIME", mMealDay)
        hashMap.put("KG_CONTENTS_DATE", simpleDateFormat.format(date))
        hashMap.put("KG_CONTENTS_CHOICE", choice)
        hashMap.put("KG_CONTENTS_ONLYCHOICE", onlyChoice)

        aq!!.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_mealChoice.php", hashMap, JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
            override fun callback(url: String?, jsonObject: JSONObject?, status: AjaxStatus?) {
                if (jsonObject != null) {
                    val good = jsonObject.getInt("good")
                    val bad = jsonObject.getInt("bad")

                    Log.i("choice", "good = $good, bad = $bad")
                }
            }
        })
    }

    fun String.splitKeeping(str: String): List<String> {
        return this.split(str).flatMap { listOf(it, str) }.dropLast(1).filterNot { it.isEmpty() }
    }

    fun String.splitKeeping(vararg strs: String): List<String> {
        var res = listOf(this)
        strs.forEach { str ->
            res = res.flatMap { it.splitKeeping(str) }
        }
        return res
    }

    // day (일,월,화,수,목,금,토)
    fun getMeals(day: Int) {
        mMealListViewAdapter!!.clear()
        Thread {
            val process = Handler(Looper.getMainLooper())
            try {
                val ssl = SSLConnect()
                ssl.postHttps("https://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100", 1000, 1000)
                val doc = Jsoup.connect("https://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100").get()
                val contents = doc.select("table.foodbox tbody tr")
                val day_contents = contents[day].children()[0].getElementsByTag("strong").text()
                process.postDelayed(Runnable {
                    val meal_days = intArrayOf(1, 3, 5)
                    for (meal_day in meal_days) {
                        val data = contents[day].children()[meal_day].toString().splitKeeping("<br>", "<td>", "</td>")
                        MainActivity.Instance.instance!!.main_title!!.text = day_contents
                        var meal_count = 0
                        val meal_content_datas = ArrayList<String>()
                        for (item in data) {
                            if (item.equals("<br>") || item.equals("<td>") || item.equals("</td>"))
                                continue
                            var m_item = ""
                            if (item[0] == ' ') {
                                for (i in 1..item.length - 1) {
                                    m_item += item[i]
                                }
                            } else {
                                m_item = item
                            }
                            meal_content_datas.add(meal_count, m_item)
                            meal_count += 1
                        }
                        if (meal_content_datas.size > 1)
                            mMealListViewAdapter!!.add(meal_day, meal_content_datas)
                    }
                }, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            process.postDelayed(Runnable {
                mMealListViewAdapter!!.notifyDataSetChanged()
            }, 0)
        }.start()
    }
}