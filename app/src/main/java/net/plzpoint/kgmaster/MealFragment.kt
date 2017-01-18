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

    var title: TextView? = null
    var data0: TextView? = null
    var data1: TextView? = null
    var data2: TextView? = null
    var data3: TextView? = null
    var data4: TextView? = null
    var data5: TextView? = null

    var meal_progress: LinearLayout? = null
    var meal_contents: LinearLayout? = null
    var no_meal_text: TextView? = null
    var no_comment_text: TextView? = null

    var meal_day0_circle: LinearLayout? = null
    var meal_day1_circle: LinearLayout? = null
    var meal_day2_circle: LinearLayout? = null

    var push_comment_text: EditText? = null
    var push_comment: Button? = null

    var load_comment_progress: ProgressBar? = null

    var mDay = 0
    var mMealDay = 0

    var aq: AQuery? = null

    var commentList: ListView? = null
    var commentAdapter: CommentAdapter? = null

    var choiceBar: SeekBar? = null
    var choiceGood: TextView? = null
    var choiceBad: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mInflater = inflater!!.inflate(R.layout.kg_meal_fragment, container, false)
        title = mInflater.kg_meal_day
        data0 = mInflater.kg_meal_data0
        data1 = mInflater.kg_meal_data1
        data2 = mInflater.kg_meal_data2
        data3 = mInflater.kg_meal_data3
        data4 = mInflater.kg_meal_data4
        data5 = mInflater.kg_meal_data5
        meal_progress = mInflater.kg_meal_progress
        meal_contents = mInflater.kg_meal_contents
        meal_day0_circle = mInflater.kg_meal_day0_circle
        meal_day0_circle!!.setOnClickListener(chageMealDay())
        meal_day1_circle = mInflater.kg_meal_day1_circle
        meal_day1_circle!!.setOnClickListener(chageMealDay())
        meal_day2_circle = mInflater.kg_meal_day2_circle
        meal_day2_circle!!.setOnClickListener(chageMealDay())
        push_comment_text = mInflater.kg_meal_push_comment_text
        push_comment = mInflater.kg_meal_push_comment_button
        push_comment!!.setOnClickListener(pushCommentListener())

        load_comment_progress = mInflater.kg_meal_commnet_progress
        no_meal_text = mInflater.kg_meal_no_meal
        no_comment_text = mInflater.kg_meal_commnet_nocomment

        commentAdapter = CommentAdapter(activity.applicationContext)
        commentList = mInflater!!.kg_meal_comment_list
        commentList!!.adapter = commentAdapter

        choiceBar = mInflater!!.kg_meal_choice_bar
        choiceBar!!.thumb.mutate().alpha = 0
        choiceBar!!.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })

        choiceBad = mInflater!!.kg_meal_choice_bad
        choiceBad!!.setOnClickListener {
            val pref = activity.getSharedPreferences("KG_MEAL", MODE_PRIVATE)
            val editor = pref.edit()
            val date = Date()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

            var data = pref!!.getBoolean(simpleDateFormat.format(date).toString() + "_" + mMealDay.toString(), false)
            if (!data) {
                mealChoice(0, 0)
                editor.putBoolean(simpleDateFormat.format(date).toString() + "_" + mMealDay.toString(), true)
                editor.commit()
            } else {
                Toast.makeText(activity.applicationContext, "이미 투표하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        choiceGood = mInflater!!.kg_meal_choice_good
        choiceGood!!.setOnClickListener {
            var pref = activity.getSharedPreferences("KG_MEAL", MODE_PRIVATE)
            var editor = pref!!.edit()
            val date = Date()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            var data = pref!!.getBoolean(simpleDateFormat.format(date).toString() + "_" + mMealDay.toString(), false)
            if (!data) {
                mealChoice(0, 1)
                editor.putBoolean(simpleDateFormat.format(date).toString() + "_" + mMealDay.toString(), true)
                editor.commit()
            } else {
                Toast.makeText(activity.applicationContext, "이미 투표하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        aq = AQuery(activity.applicationContext)

        val oCalendar = Calendar.getInstance()
        //val week = arrayOf("일", "월", "화", "수", "목", "금", "토")
        var dayOfWeek = oCalendar.get(Calendar.DAY_OF_WEEK) - 1

        //println("현재 요일: " + week[oCalendar.get(Calendar.DAY_OF_WEEK) - 1] + "요일")

        mDay = dayOfWeek
        mMealDay = 0

        getMeals(mDay, mMealDay)

        commentList!!.visibility = VISIBLE
        no_comment_text!!.visibility = GONE
        load_comment_progress!!.visibility = GONE

        //val pref = activity.getSharedPreferences("KG_MEAL", MODE_PRIVATE)
        //val editor = pref.edit()
        //editor.clear()
        //editor.commit()

        loadComment()

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

        choiceBar!!.max = 100
        choiceBar!!.progress = 0

        aq!!.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_mealChoice.php", hashMap, JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
            override fun callback(url: String?, jsonObject: JSONObject?, status: AjaxStatus?) {
                if (jsonObject != null) {
                    val good = jsonObject.getInt("good")
                    val bad = jsonObject.getInt("bad")

                    choiceBar!!.max = good + bad
                    choiceBar!!.progress = good

                    Log.i("choice", "good = $good, bad = $bad")
                }
            }
        })
    }

    fun chageMealDay(): View.OnClickListener {
        val change = object : View.OnClickListener {
            override fun onClick(v: View?) {
                when (v!!.id) {
                    R.id.kg_meal_day0_circle -> {
                        mMealDay = 0
                    }
                    R.id.kg_meal_day1_circle -> {
                        mMealDay = 1
                    }
                    R.id.kg_meal_day2_circle -> {
                        mMealDay = 2
                    }
                }
                no_meal_text!!.visibility = GONE
                getMeals(mDay, mMealDay)
                loadComment()
                mealChoice(1)
            }
        }
        return change
    }

    fun pushCommentListener(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {
                val pushText = push_comment_text!!.text.toString()
                val hashMap = HashMap<String, Any>()

                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(push_comment_text!!.windowToken, 0)

                val date = Date()
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

                hashMap.put("KG_ID", (activity.findViewById(R.id.kg_profile_nickname) as TextView).text)
                hashMap.put("KG_COMMENT", pushText)
                hashMap.put("KG_CONTENTS_DATE", simpleDateFormat.format(date))
                hashMap.put("KG_CONTENTS_ID", 1)
                hashMap.put("KG_CONTENTS_TIME", mMealDay)
                aq!!.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_pushComment.php", hashMap, JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
                    override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {

                    }
                })

                push_comment_text!!.text.clear()
                loadComment()
            }
        }
    }

    // 불러오기
    fun loadComment() {
        commentAdapter!!.clear()
        commentAdapter!!.notifyDataSetChanged()
        Thread {
            val progress = Handler(Looper.getMainLooper())
            progress.postDelayed(Runnable {
                commentList!!.visibility = GONE
                load_comment_progress!!.visibility = VISIBLE
                no_comment_text!!.visibility = GONE
            }, 0)

            // 콘텐츠 아이디 2
            // 날자 2017-01-24
            // 콘텐츠 시간 0
            val hashMap = HashMap<String, Any>()

            val date = Date()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

            hashMap.put("KG_CONTENTS_ID", 1)
            hashMap.put("KG_CONTENTS_DATE", simpleDateFormat.format(date))
            hashMap.put("KG_CONTENTS_TIME", mMealDay)

            aq!!.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_loadComment.php", hashMap, JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
                override fun callback(url: String?, jsonObject: JSONObject?, status: AjaxStatus?) {
                    if (jsonObject != null) {
                        val state = jsonObject.getString("state")
                        Log.i("state", state.toString())
                        if (state == "true") {
                            val jsonArray = jsonObject.getJSONArray("comment_list")
                            if (jsonArray != null) {
                                for (i in 0..jsonArray.length() - 1) {
                                    val container = jsonArray.getJSONObject(i)
                                    val id = container.getString("id")
                                    val comment = container.getString("comment")
                                    val date = container.getString("date")

                                    Log.i("comment", "$id $comment $date")

                                    commentAdapter!!.addComment(CommentData(id.toString(), comment.toString(), date.toString()))
                                }
                                if (commentAdapter!!.comments.size > 0) {
                                    commentAdapter!!.reverse()
                                    commentAdapter!!.notifyDataSetChanged()
                                    load_comment_progress!!.visibility = GONE
                                    no_comment_text!!.visibility = GONE
                                    commentList!!.visibility = VISIBLE
                                }
                            }
                        } else {
                            load_comment_progress!!.visibility = GONE
                            no_comment_text!!.visibility = VISIBLE
                            commentList!!.visibility = GONE
                        }
                    }
                }
            })
        }.start()
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
    // mealDay (아침,점심,저녁)
    fun getMeals(day: Int, _mealDay: Int) {
        var mealDay = _mealDay
        var isMeal = false

        no_meal_text!!.visibility = GONE
        meal_contents!!.visibility = GONE

        if (_mealDay == 0) {
            mealDay = 1
            title!!.text = "아침"
            meal_day0_circle!!.setBackgroundResource(R.drawable.circle_shape_black)
            meal_day1_circle!!.setBackgroundResource(R.drawable.circle_shape_white)
            meal_day2_circle!!.setBackgroundResource(R.drawable.circle_shape_white)
        } else if (_mealDay == 1) {
            mealDay = 3
            title!!.text = "점심"
            meal_day0_circle!!.setBackgroundResource(R.drawable.circle_shape_white)
            meal_day1_circle!!.setBackgroundResource(R.drawable.circle_shape_black)
            meal_day2_circle!!.setBackgroundResource(R.drawable.circle_shape_white)
        } else if (_mealDay == 2) {
            mealDay = 5
            title!!.text = "저녁"
            meal_day0_circle!!.setBackgroundResource(R.drawable.circle_shape_white)
            meal_day1_circle!!.setBackgroundResource(R.drawable.circle_shape_white)
            meal_day2_circle!!.setBackgroundResource(R.drawable.circle_shape_black)
        }

        Thread {
            try {
                val process = Handler(Looper.getMainLooper())
                val ssl = SSLConnect()
                ssl.postHttps("https://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100", 1000, 1000)
                val doc = Jsoup.connect("https://www.game.hs.kr/~game/2013/inner.php?sMenu=E4100").get()
                val contents = doc.select("table.foodbox tbody tr")
                val day_contents = contents[day].children()[0].getElementsByTag("strong").text()
                val data = contents[day].children()[mealDay].toString().splitKeeping("<br>", "<td>", "</td>")
                process.postDelayed(Runnable {
                    MainActivity.Instance.instance!!.main_title!!.text = day_contents
                    var meal_count = 0
                    if (!isMeal) {
                        for (item in data) {
                            if (item.equals("<br>") || item.equals("<td>") || item.equals("</td>"))
                                continue

                            var emItem = ""
                            if (item[0] == ' ') {
                                for (i in 1..item.length - 1) {
                                    emItem += item[i]
                                }
                            } else
                                emItem = item

                            Log.i("Item", emItem)

                            isMeal = true
                            when (meal_count) {
                                0 -> {
                                    data0!!.text = emItem
                                }
                                1 -> {
                                    data1!!.text = emItem
                                }
                                2 -> {
                                    data2!!.text = emItem
                                }
                                3 -> {
                                    data3!!.text = emItem
                                }
                                4 -> {
                                    data4!!.text = emItem
                                }
                                5 -> {
                                    data5!!.text = item
                                }
                            }
                            meal_count += 1
                        }
                    }
                    if (isMeal) {
                        process.postDelayed(Runnable {
                            meal_progress!!.visibility = GONE
                            meal_contents!!.visibility = VISIBLE
                            no_meal_text!!.visibility = GONE
                            meal_contents!!.visibility = VISIBLE
                        }, 0)
                    } else {
                        process.postDelayed(Runnable {
                            meal_progress!!.visibility = GONE
                            meal_contents!!.visibility = VISIBLE
                            no_meal_text!!.visibility = VISIBLE
                            meal_contents!!.visibility = GONE
                        }, 0)
                    }
                }, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}