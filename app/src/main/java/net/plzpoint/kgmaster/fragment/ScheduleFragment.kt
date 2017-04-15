package net.plzpoint.kgmaster.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.kg_schedule_fragment.view.*
import net.plzpoint.kgmaster.activity.MainActivity
import net.plzpoint.kgmaster.R
import net.plzpoint.kgmaster.utils.SSLConnect
import org.jsoup.Jsoup
import java.util.*

/**
 * Created by junsu on 2016-12-30.
 */

class ScheduleFragment : Fragment() {

    class ScheduleData {
        var day: String = ""
        var text: String = ""

        constructor() {

        }

        constructor(day: String, text: String) {
            this.day = day
            this.text = text
        }
    }

    class ScheduleHolder(view: View) {
        val day: TextView
        val text: TextView

        init {
            day = view!!.findViewById(R.id.schedule_day) as TextView
            text = view!!.findViewById(R.id.schedule_text) as TextView
            text.isSelected = true
        }
    }

    class ScheduleAdapter(context: Context) : BaseAdapter() {
        val inflate: LayoutInflater
        val schedules = ArrayList<ScheduleData>()

        init {
            inflate = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return schedules.count()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return schedules[position]
        }

        fun addSchedule(schedule: ScheduleData) {
            schedules.add(schedule)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View?
            val holder: ScheduleHolder
            if (convertView == null) {
                view = inflate!!.inflate(R.layout.kg_schedule_contents, parent, false)
                holder = ScheduleHolder(view)
                view!!.tag = holder
            } else {
                view = convertView
                holder = view!!.tag as ScheduleHolder
            }

            holder.day.text = schedules[position].day
            holder.text.text = schedules[position].text
            return view
        }
    }

    fun instance(): ScheduleFragment {
        val fragment = ScheduleFragment()
        return fragment
    }

    // ================================

    var scheduleList: ListView? = null
    var scheduleAdapter: ScheduleAdapter? = null
    var scheduleProgress: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mInflater = inflater!!.inflate(R.layout.kg_schedule_fragment, container, false)

        scheduleProgress = mInflater!!.kg_scheul_progress
        scheduleAdapter = ScheduleAdapter(activity.applicationContext)
        scheduleList = mInflater.kg_schedule_list
        scheduleList!!.adapter = scheduleAdapter

        getSchedule()

        MainActivity.Instance.instance!!.main_title!!.text = "일정"
        MainActivity.Instance.instance!!.kg_button_layout!!.visibility = View.INVISIBLE

        return mInflater
    }

    fun getSchedule() {
        Thread {
            val progress = Handler(Looper.getMainLooper())
            progress.postDelayed(Runnable {
                scheduleProgress!!.visibility = View.VISIBLE
                scheduleList!!.visibility = View.GONE
            }, 0)

            try {
                val ssl = SSLConnect()
                ssl.postHttps("https://www.game.hs.kr/~game/2013/inner.php?sMenu=D1000", 1000, 1000)
                val doc = Jsoup.connect("https://www.game.hs.kr/~game/2013/inner.php?sMenu=D1000").get()
                val contents = doc.select("table.calendar_box tbody tr td")
                for (main_contents in contents) {
                    var main_contents_day = ""
                    val day = main_contents.select("img")
                    for (item in day) {
                        val alt = item.attr("alt")
                        if (alt.equals("닫기"))
                            continue
                        main_contents_day += alt
                    }
                    val main_contents_schedule = main_contents.select("ul.festival li div.detailview dl dd")
                    for (item in main_contents_schedule) {
                        scheduleAdapter!!.addSchedule(ScheduleData(main_contents_day, main_contents_schedule.text()))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            progress.postDelayed(Runnable {
                scheduleList!!.visibility = View.VISIBLE
                scheduleProgress!!.visibility = View.GONE
                scheduleAdapter!!.notifyDataSetChanged()
            }, 0)
        }.start()
    }
}