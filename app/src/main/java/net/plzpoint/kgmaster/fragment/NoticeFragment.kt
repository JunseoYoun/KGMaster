package net.plzpoint.kgmaster.fragment

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.widget.*
import kotlinx.android.synthetic.main.kg_notice_fragment.view.*
import org.jsoup.Jsoup
import java.util.*
import android.webkit.WebViewClient
import android.webkit.WebView
import android.content.Intent
import android.view.KeyEvent
import net.plzpoint.kgmaster.activity.MainActivity
import net.plzpoint.kgmaster.R
import net.plzpoint.kgmaster.utils.SSLConnect


/**
 * Created by junsu on 2016-12-31.
 */

class NoticeFragment : Fragment() {

    private inner class WishWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    class NoticeData {
        var day: String = ""
        var text: String = ""
        var view_count: String = ""
        var tag: String = ""

        constructor() {

        }

        constructor(day: String, text: String, view: String, tag: String) {
            this.day = day
            this.text = text
            this.view_count = view
            this.tag = tag
        }
    }

    class NoticeHolder(view: View?) {
        val day: TextView
        val text: TextView
        val view_count: TextView

        init {
            day = view!!.findViewById(R.id.kg_notice_day) as TextView
            text = view!!.findViewById(R.id.kg_notice_text) as TextView
            view_count = view!!.findViewById(R.id.kg_notice_viewcount) as TextView
        }
    }

    class NoticeAdapter(context: Context) : BaseAdapter() {

        val notices = ArrayList<NoticeData>()
        val inflater: LayoutInflater

        var noticeListener: NoticeItemClickListener? = null

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return notices.count()
        }


        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return notices[position]
        }

        fun addNotice(data: NoticeData) {
            notices.add(data)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View?
            var holder: NoticeHolder?
            if (convertView == null) {
                view = inflater!!.inflate(R.layout.kg_notice_contents, parent, false)
                holder = NoticeHolder(view)
                view!!.tag = holder
            } else {
                view = convertView
                holder = view!!.tag as NoticeHolder
            }

            holder!!.day.text = notices[position].day
            holder!!.text.text = notices[position].text
            holder!!.view_count.text = notices[position].view_count

            view.setOnClickListener {
                noticeListener!!.onNoticeListener(notices[position])
            }

            return view
        }
    }

    interface NoticeItemClickListener {
        fun onNoticeListener(data: NoticeData) {

        }
    }

    fun instance(): NoticeFragment {
        val noticeFragment = NoticeFragment()
        return noticeFragment
    }

    var noticeAdapter: NoticeAdapter? = null
    var noticeList: ListView? = null
    var noticeProgress: ProgressBar? = null

    var webIntent: Intent? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val mInflater = inflater!!.inflate(R.layout.kg_notice_fragment, container, false)
        mInflater.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                }
                return true
            }
        })

        noticeProgress = mInflater!!.kg_notice_progress
        noticeAdapter = NoticeAdapter(activity.applicationContext)
        noticeAdapter!!.noticeListener = object : NoticeItemClickListener {
            override fun onNoticeListener(data: NoticeData) {
                try {
                    val ssl = SSLConnect()
                    ssl.postHttps(data.tag, 10000, 10000)
                    val uri = Uri.parse(data.tag)

                    webIntent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(webIntent)
                } catch (e: Exception) {
                    Log.e("Error", "Notice Web View")
                    e.printStackTrace()
                }
                super.onNoticeListener(data)
            }
        }

        noticeList = mInflater!!.kg_notice_list
        noticeList!!.adapter = noticeAdapter

        MainActivity.Instance.instance!!.main_title!!.text = "공지"

        getNotice()

        return mInflater
    }

    fun getNotice() {
        Thread {
            val progress = Handler(Looper.getMainLooper())
            progress.postDelayed(Runnable {
                noticeProgress!!.visibility = VISIBLE
                noticeList!!.visibility = GONE
            }, 0)
            try {
                val ssl = SSLConnect()
                ssl.postHttps("https://www.game.hs.kr/2013/inner.php?sMenu=G1000&pno=1", 1000, 100)
                val doc = Jsoup.connect("https://www.game.hs.kr/2013/inner.php?sMenu=G1000&pno=1").get()
                val contents = doc.select("div.boardnew2011 tbody tr")
                progress.postDelayed(Runnable {
                    for (main_contents in contents) {
                        val day = main_contents.select("td")[2].text()
                        val temp_text = main_contents.select("td")[0].select("a")
                        val text = temp_text.text()
                        val tag = "https://www.game.hs.kr/2013/" + temp_text.attr("href")
                        Log.i("Tag", tag)
                        val view = main_contents.select("td")[3].text()
                        noticeAdapter!!.addNotice(NoticeData(day, text, "조회수 : " + view, tag))
                    }
                }, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            progress.postDelayed(Runnable {
                noticeProgress!!.visibility = GONE
                noticeList!!.visibility = VISIBLE
                noticeAdapter!!.notifyDataSetChanged()
            }, 0)
        }.start()
    }
}