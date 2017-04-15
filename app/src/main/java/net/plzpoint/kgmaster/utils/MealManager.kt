package net.plzpoint.kgmaster.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.*

open class MealManager(context: Context) {
    val url = "https://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100&date="
    val url_food = "table.foodbox tbody tr"
    var aq: AQuery
    var pref: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    val context: Context

    init {
        aq = AQuery(context)
        pref = context.getSharedPreferences("KGMealChoice", Context.MODE_PRIVATE)
        edit = pref!!.edit()
        this.context = context
    }

    open class MealData {
        var day: String = ""
        var data0: String = ""
        var data1: String = ""
        var data2: String = ""
        var data3: String = ""
        var data4: String = ""
        var data5: String = ""
        var mealMonthDay = ""

        var goodChoice = 0
        var badChoice = 0

        var goodCallback = object : View.OnClickListener {
            override fun onClick(p0: View?) {

            }
        }
        var badCallback = object : View.OnClickListener {
            override fun onClick(p0: View?) {

            }
        }

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

    // 가져온 급식마다 callback을 호출한다. ( (아침, 점심, 저녁), 밥, 반찬1, 반찬2, 반찬3 ... )
    // day = dayOfWeek
    // mealDay
    // -1. 아침, 점심, 저녁
    // 1. 아침
    // 2. 점심
    // 3. 저녁
    fun getMeal(date: String, day: Int, mealDay: Int = -1, callback: ((md: MealData?, time: Int?, success: Boolean) -> Unit)) {
        Thread {
            try {
                var newUrl = url + date
                var meal_time_counter = 0
                val ssl = SSLConnect()
                ssl.postHttps(newUrl, 1000, 1000)
                val doc = Jsoup.connect(newUrl).get()
                val contents = doc.select(url_food)
                val day_contents = contents[day].children()[0].getElementsByTag("strong").text()
                val process = Handler(Looper.getMainLooper())
                process.postDelayed(Runnable {
                    var meal_days = ArrayList<Int>()
                    if (mealDay == -1) {
                        meal_days.add(1)
                        meal_days.add(3)
                        meal_days.add(5)
                    } else
                        meal_days.add(mealDay)

                    for (meal_day in meal_days) {
                        val data = contents[day].children()[meal_day].toString().splitKeeping("<br>", "<td>", "</td>")
                        // MainActivity.Instance.instance!!.main_title!!.text = day_contents
                        var meal_count = 0
                        val meal_content_datas = ArrayList<String>()
                        for (item in data) {
                            if (item.equals("<br>") || item.equals("<td>") || item.equals("</td>"))
                                continue
                            var m_item = ""
                            if (item[0] == ' ')
                                for (i in 1..item.length - 1)
                                    m_item += item[i]
                            else
                                m_item = item
                            meal_content_datas.add(meal_count, m_item)
                            meal_count += 1
                        }
                        if (meal_content_datas.size > 1) {
                            var day_string = "아침"
                            if (meal_day == 3)
                                day_string = "점심"
                            else if (meal_day == 5)
                                day_string = "저녁"
                            val meal = MealData(day_string, meal_content_datas[0], meal_content_datas[1], meal_content_datas[2], meal_content_datas[3], meal_content_datas[4], meal_content_datas[5])

                            meal.mealMonthDay = day_contents

                            callback.invoke(meal, meal_time_counter, true)
                            meal_time_counter += 1
                        } else {
                            callback.invoke(null, null, false)
                        }
                    }
                }, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun getChoice(date: String, time: Int, callback: (good: Int?, bad: Int?) -> Unit) {
        aq.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_getMeal.php?KG_CONTENTS_DATE=" + date, JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
            override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {
                if (`object` != null) {
                    val jsonObject: JSONObject = `object`.getJSONObject("m" + time.toString())
                    callback.invoke((jsonObject["good"] as String).toInt(), (jsonObject["bad"] as String).toInt())
                }
            }
        })
    }

    fun setChoice(date: String, time: Int, choice: Int, callback: (good: Int?, bad: Int?) -> Unit) {
        val kgmealdatachoice = date + time.toString()
        val kgmealresult = pref!!.getBoolean(kgmealdatachoice, false)
        if (!kgmealresult) {
            aq.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_setMeal.php?KG_CONTENTS_DATE=" + date +
                    "&KG_CONTENTS_TIME=" + time.toString() +
                    "&KG_CONTENTS_CHOICE=" + choice.toString(), JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
                override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {
                    if (`object` != null) {
                        callback.invoke((`object`["good"] as String).toInt(), (`object`["bad"] as String).toInt())
                        edit!!.putBoolean(kgmealdatachoice, true)
                        edit!!.commit()
                        Toast.makeText(context, kgmealdatachoice + " 에 투표해주셔서 감사합니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Toast.makeText(context, "이미 투표하셨습니다.", Toast.LENGTH_SHORT).show()
        }
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
}