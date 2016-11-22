package net.plzpoint.kgmaster.Util

import org.jsoup.Jsoup

import java.net.URL;

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection


/**
 * Created by junsu on 2016-11-22.
 * 앱 스토어에 등록되어있는 앱에서 Version을 가져온다.
 */

public class VersionChecker : Thread() {
    companion object {
        fun getMarketVersion(packageName: String): String? {
            try {
                val document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get()
                val Version = document.select(".content")
                for (element in Version) {
                    if (element.attr("itemprop").equals("softwareVersion")) {
                        return element.text().trim()
                    }
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            return null
        }

        fun getMarketVersionFast(packageName: String): String? {
            var mData = ""
            var mVer: String? = null
            try {
                val mUrl = URL("https://play.google.com/store/apps/details?id=" + packageName)
                val mConnection = mUrl.openConnection() as HttpURLConnection ?: return null

                mConnection.setConnectTimeout(5000)
                mConnection.setUseCaches(false)
                mConnection.setDoOutput(true)

                if (mConnection.getResponseCode() === HttpURLConnection.HTTP_OK) {
                    val bufferedReader = BufferedReader(InputStreamReader(mConnection.getInputStream()))
                    while (true) {
                        val line = bufferedReader.readLine() ?: break
                        mData += line
                    }
                    bufferedReader.close()
                }
                mConnection.disconnect()
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
            val startToken = "softwareVersion\">"
            val endToken = "<"
            val index = mData.indexOf(startToken)
            if (index == -1) {
                mVer = null
            } else {
                mVer = mData.substring(index + startToken.length, index + startToken.length + 100)
                mVer = mVer.substring(0, mVer.indexOf(endToken)).trim { it <= ' ' }
            }
            return mVer
        }
    }
}