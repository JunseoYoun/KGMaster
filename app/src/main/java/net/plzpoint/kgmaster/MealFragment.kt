package net.plzpoint.kgmaster

import android.app.Fragment
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
import android.widget.*
import com.androidquery.AQuery
import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import kotlinx.android.synthetic.main.kg_meal_fragment.*
import kotlinx.android.synthetic.main.kg_meal_fragment.view.*
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.*

class MealFragment : Fragment() {

    fun instance(): MealFragment {
        val fragment = MealFragment()
        return fragment
    }

    // =======================================================

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

    var meal_day0_circle: LinearLayout? = null
    var meal_day1_circle: LinearLayout? = null
    var meal_day2_circle: LinearLayout? = null

    var push_comment_text: EditText? = null
    var push_comment: Button? = null

    var load_comment: Button? = null
    var load_comment_progress: ProgressBar? = null

    var mDay = 0
    var mMealDay = 0

    var aq: AQuery? = null

    var commentListPanel: LinearLayout? = null
    var commentPanel: LinearLayout? = null
    var commentList: ListView? = null
    var commentAdapter: CommentAdapter? = null

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
        load_comment = mInflater.kg_meal_comment_load
        load_comment!!.setOnClickListener(loadCommentListener())
        load_comment_progress = mInflater.kg_meal_commnet_progress
        no_meal_text = mInflater.kg_meal_no_meal

        commentPanel = mInflater!!.kg_meal_comment_panel
        commentListPanel = mInflater!!.kg_comment_list_panel

        commentAdapter = CommentAdapter(activity.applicationContext)
        commentList = mInflater!!.kg_meal_comment_list
        commentList!!.adapter = commentAdapter

        aq = AQuery(activity.applicationContext)

        mDay = 0
        mMealDay = 0

        getMeals(mDay, mMealDay)

        return mInflater
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
            }
        }
        return change
    }

    fun pushCommentListener(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {
                val pushText = push_comment_text!!.text.toString()
                val hashMap = HashMap<String, Any>()
                hashMap.put("KG_ID", "root")
                hashMap.put("KG_COMMENT", "ASDASDASD")
                hashMap.put("KG_CONTENTS_ID", 2)

                aq!!.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_pushComment.php", hashMap, JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
                    override fun callback(url: String?, `object`: JSONObject?, status: AjaxStatus?) {

                    }
                })
            }
        }
    }

    // 불러오기 버튼
    fun loadCommentListener(): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View?) {
                loadComment()
            }
        }
    }

    // 불러오기
    fun loadComment() {
        var isComment = false
        Thread {
            val progress = Handler(Looper.getMainLooper())
            progress.postDelayed(Runnable {
                load_comment_progress!!.visibility = VISIBLE
                load_comment!!.visibility = GONE
            }, 0)

            aq!!.ajax("http://junsueg5737.dothome.co.kr/KGMaster/KGMaster_loadComment.php", JSONObject().javaClass, object : AjaxCallback<JSONObject>() {
                override fun callback(url: String?, jsonObject: JSONObject?, status: AjaxStatus?) {
                    if (jsonObject != null) {
                        val jsonArray = jsonObject.getJSONArray("comment_list")
                        if (jsonArray != null) {
                            for (i in 0..jsonArray.length() - 1) {
                                val container = jsonArray.getJSONObject(i)
                                val id = container.getString("id")
                                val comment = container.getString("comment")
                                val date = container.getString("date")

                                Log.i("Item", "$id $comment $date")

                                commentAdapter!!.addComment(CommentData(id, comment, date))

                                isComment = true
                            }
                        }
                    }
                }
            })

            progress.postDelayed(Runnable {
                load_comment_progress!!.visibility = GONE
                load_comment!!.visibility = GONE
                if (isComment) {
                    commentPanel!!.visibility = GONE
                    commentListPanel!!.visibility = VISIBLE
                    commentAdapter!!.notifyDataSetChanged()
                } else {
                    commentListPanel!!.visibility = GONE
                    commentPanel!!.visibility = VISIBLE
                }
            }, 0)
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
                            Log.i("Item", item)

                            isMeal = true
                            when (meal_count) {
                                0 -> {
                                    data0!!.text = item
                                }
                                1 -> {
                                    data1!!.text = item
                                }
                                2 -> {
                                    data2!!.text = item
                                }
                                3 -> {
                                    data3!!.text = item
                                }
                                4 -> {
                                    data4!!.text = item
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