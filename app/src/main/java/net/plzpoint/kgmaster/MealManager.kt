package net.plzpoint.kgmaster

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.jsoup.Jsoup
import java.util.*

open class MealManager {
    val url = "https://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100"
    val url_food = "table.foodbox tbody tr"

    open class MealData {
        var day: String = ""
        var data0: String = ""
        var data1: String = ""
        var data2: String = ""
        var data3: String = ""
        var data4: String = ""
        var data5: String = ""
        var mealMonthDay = ""

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
    // day = ayOfWeek

    // mealDay
    // -1. 아침, 점심, 저녁
    // 1. 아침
    // 2. 점심
    // 3. 저녁
    fun getMeal(day: Int, mealDay: Int = -1, callback: ((MealManager.MealData) -> Unit)) {
        Thread {
            try {
                val ssl = SSLConnect()
                ssl.postHttps(url, 1000, 1000)
                val doc = Jsoup.connect(url).get()
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
                            val meal = MealManager.MealData(day_string, meal_content_datas[0], meal_content_datas[1], meal_content_datas[2], meal_content_datas[3], meal_content_datas[4], meal_content_datas[5])

                            meal.mealMonthDay = day_contents

                            callback.invoke(meal)
                        }
                    }
                }, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    // 아무것도 넣지 않거나 -1이면 만족도만 가져온다.
    fun getChoice(dayTime: Int = -1, callback: (good: Int, bad: Int) -> Unit) {
        callback.invoke(0, 0)
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