package net.plzpoint.kgmaster.Util

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import net.plzpoint.kgmaster.MainActivity


/**
 * Created by junsu on 2016-11-22.
 * TODO : [버전 업데이트] 급식앱 -> KG Master로 교체
 * TODO : [버전 업데이트] 베타버전으로 여러번 테스트 요망 S7에서 안됨
 */

class VersionProcess(mainActivity: MainActivity) : Thread() {
    val mainActivity : MainActivity
    val versionHandler : Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    val updateDialog: AlertDialog.Builder
                    updateDialog = AlertDialog.Builder(mainActivity.applicationContext)
                    updateDialog.setTitle("업데이트")
                    updateDialog.setMessage("급식앱의 새로운 버전이 있습니다.\n보다 나은 사용을 위해 업데이트 해 주세요.")
                    updateDialog.setNegativeButton("확인", DialogInterface.OnClickListener { dialog, which ->
                        val marketLaunch = Intent(Intent.ACTION_VIEW)
                        marketLaunch.data = Uri.parse("market://details?id=" + mainActivity.packageName)
                        mainActivity.startActivity(marketLaunch)
                    })
                    updateDialog.setPositiveButton("취소", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                    updateDialog.show()
                }
            }
        }
    }

    init {
        this.mainActivity = mainActivity
    }

    override fun run() {
        val storeVersion = VersionChecker.getMarketVersion(mainActivity.packageName)
        try {
            mainActivity.deviceVersion = mainActivity.packageManager.getPackageInfo(mainActivity.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (storeVersion!!.compareTo(mainActivity.deviceVersion) > 0) {
            versionHandler.sendMessage(Message.obtain(versionHandler, 0))
        }
    }
}