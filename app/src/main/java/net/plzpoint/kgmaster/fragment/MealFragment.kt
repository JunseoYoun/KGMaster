package net.plzpoint.kgmaster.Fragment

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.plzpoint.kgmaster.R
import java.util.*
import android.app.ProgressDialog
import android.os.Debug
import android.os.Handler
import android.os.Looper.getMainLooper
import com.androidquery.AQuery
import kotlinx.android.synthetic.main.kg_meal_contents.*
import java.io.InputStreamReader
import java.net.URL
import net.htmlparser.jericho.Element
import net.htmlparser.jericho.HTMLElementName
import net.htmlparser.jericho.Source


open class MealFragment : Fragment() {

    fun newInstance(): MealFragment {
        val mealFragment = MealFragment()
        return mealFragment
    }

    // 동적으로 계속해서 바뀜 일주일 단위
    var curYear: Int = 0
    var curMonth: Int = 0
    var curDay: Int = 0
    var todayString: String = ""

    //var sharedData: SharedPreferences = this.activity.getSharedPreferences("Meal", 0)
    //var sharedDataEditor: SharedPreferences.Editor = sharedData.edit()

    val weeks = arrayOf("일", "월", "화", "수", "목", "금", "토")

    var mealProgressDialog: ProgressDialog? = null

    val URL_SCHOOL_MEAL = "http://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100"
    var url: URL? = null
    var source: Source? = null
    var aq: AQuery? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        aq = AQuery(activity)

        calToday()
        mealProcess()
        return inflater!!.inflate(R.layout.kg_meal_contents, container, false)
    }

    fun calToday() {
        val calender = Calendar.getInstance()

        curYear = calender.get(Calendar.YEAR)
        curMonth = calender.get(Calendar.MONTH) - Calendar.JANUARY + 1
        curDay = calender.get(Calendar.DAY_OF_MONTH)

        todayString = curMonth.toString() + "월"
        todayString += " "
        todayString += curDay.toString() + "일"
        todayString += "(" + weeks[calender.get(Calendar.DAY_OF_WEEK) - 1] + ")"

        Log.i("날자", todayString)
    }

    // 급식을 불러온다.
    fun mealProcess() {
        Thread {
            val Progress = Handler(getMainLooper())
            Progress.postDelayed(Runnable {
                mealProgressDialog = ProgressDialog.show(activity, "", "급식을 가져오는 중 입니다\n잠시만 기다려 주세요")
            }, 0)

            try {
                url = URL(URL_SCHOOL_MEAL)
                val html = url!!.openStream()
                source = Source(InputStreamReader(html, "utf-8"))
                source!!.fullSequentialParse()
            } catch (e: Exception) {
                Log.d("Error", e.toString() + " ")
            }

            val MEAL_TABLE = source!!.getAllElements(HTMLElementName.TABLE).get(0)
            val MEAL_TBODY = MEAL_TABLE.getAllElements(HTMLElementName.TBODY).get(0)

            var mealTime = 0
            // 저녁만 있을 떄
            var onlyDinner = false

            for (tr in MEAL_TBODY.getAllElements(HTMLElementName.TR)) {
                val MEAL_STRON = tr.getAllElements(HTMLElementName.STRONG).get(0)
                if (MEAL_STRON.textExtractor.toString().equals(todayString)) {
                    Log.i("오늘의 날자", MEAL_STRON.textExtractor.toString())
                    for (td in tr.getAllElements(HTMLElementName.TD)) {
                        val FINAL_MEAL = td.textExtractor.toString()
                        val FINAL_MEALS = FINAL_MEAL.split(" 밥", " ")

                        Progress.postDelayed(Runnable {
                            var isMeal = false
                            var mealDataIdx = 0

                            for (item in FINAL_MEALS) {
                                if (item.equals(""))
                                    continue
                                val curData = StringBuffer(item)

                                // 흰 밥을 하나로 표시하기 위한 노가다
                                var idx = -1
                                var tempBob = false
                                idx = curData.indexOf("흰밥")
                                if (idx !== -1) {
                                    tempBob = true
                                }
                                idx = -1
                                idx = curData.indexOf("흰")
                                if (idx !== -1) {
                                    if (!tempBob)
                                        curData.insert(idx + 1, " 밥")
                                    else
                                        curData.insert(1, " ")
                                }

                                when (mealDataIdx) {
                                    0 -> kg_meal_item1.text = curData.toString()
                                    1 -> kg_meal_item2.text = curData.toString()
                                    2 -> kg_meal_item3.text = curData.toString()
                                    3 -> kg_meal_item4.text = curData.toString()
                                    4 -> kg_meal_item5.text = curData.toString()
                                    5 -> kg_meal_item6.text = curData.toString()
                                }
                                mealDataIdx += 1
                            }
                        }, 0)
                    }
                }
            }
        }.start()
    }
}