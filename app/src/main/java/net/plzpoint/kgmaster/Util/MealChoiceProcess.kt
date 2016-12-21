package net.plzpoint.kgmaster.Util

import android.util.Log
import android.widget.SeekBar
import com.androidquery.AQuery
import net.plzpoint.kgmaster.Fragment.MealFragment
import java.util.*

import com.androidquery.callback.AjaxCallback
import com.androidquery.callback.AjaxStatus
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.logging.Handler


/**
 * Created by junsu on 2016-12-19.
 */

enum class ChoiceTime {
    Morning, Lunch, Evening, None
}

enum class ChoiceType {
    Good, Bad, None
}

open class MealChoiceProcess : Thread {
    private var choiceTime: ChoiceTime = ChoiceTime.None
    private var choiceType: ChoiceType = ChoiceType.None
    private var choiceBar: SeekBar? = null
    private var onlyViewChoice = false
    private var aq: AQuery? = null
    private var todayString = ""

    constructor(choiceBar: SeekBar) {
        this.choiceBar = choiceBar
        aq = MealFragment.mealFragmentInstance.instance!!.aq
    }

    fun choiceView(choiceTime: ChoiceTime) {
        onlyViewChoice = true
        this.choiceTime = choiceTime
        this.choiceType = ChoiceType.None
        todayString = MealFragment.mealFragmentInstance.instance!!.todayString
        run()
    }

    fun choice(choiceTime: ChoiceTime, choiceType: ChoiceType) {
        onlyViewChoice = false
        this.choiceTime = choiceTime
        this.choiceType = choiceType
        todayString = MealFragment.mealFragmentInstance.instance!!.todayString
        run()
    }

    override fun run() {
        val hashMap = HashMap<String, Object>()
        val objectTest: Object = choiceTime.toString() as Object
        Log.i("ASD", objectTest.toString())
        hashMap.put("Time", choiceTime.toString() as Object)
    }
}