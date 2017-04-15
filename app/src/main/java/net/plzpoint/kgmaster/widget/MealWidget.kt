package net.plzpoint.kgmaster.widget

import android.R.attr.*
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import java.util.*
import android.view.View
import android.content.ComponentName
import android.graphics.Color
import android.opengl.Visibility
import net.plzpoint.kgmaster.R
import net.plzpoint.kgmaster.utils.MealManager
import java.text.SimpleDateFormat


/**
 * Implementation of App Widget functionality.
 */
class MealWidget : AppWidgetProvider() {
    var mealManager: MealManager? = null

    var mealDay: Int = 1

    val mealDayText0Action = "net.plzpoint.kgmaster.pendingAction0"
    val mealDayText1Action = "net.plzpoint.kgmaster.pendingAction1"
    val mealDayText2Action = "net.plzpoint.kgmaster.pendingAction2"

    var year = ""
    var month = ""
    var day = ""
    var masterDay = ""
    var simpleDateFormat = SimpleDateFormat("yyyy-MM")

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        mealManager = MealManager(context)

        simpleDateFormat = SimpleDateFormat("yyyy")
        year = simpleDateFormat.format(Date())
        simpleDateFormat = SimpleDateFormat("MM")
        month = simpleDateFormat.format(Date())
        simpleDateFormat = SimpleDateFormat("dd")
        day = simpleDateFormat.format(Date())
        masterDay = year.plus("-").plus(month).plus("-").plus(day)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.kg_meal_widget)
            views.setOnClickPendingIntent(R.id.kg_meal_widget_day0, getPendingSelfIntent(context, mealDayText0Action))
            views.setOnClickPendingIntent(R.id.kg_meal_widget_day1, getPendingSelfIntent(context, mealDayText1Action))
            views.setOnClickPendingIntent(R.id.kg_meal_widget_day2, getPendingSelfIntent(context, mealDayText2Action))

            val oCalendar = Calendar.getInstance()
            val dayOfWeek = oCalendar.get(Calendar.DAY_OF_WEEK) - 1
            val plusText = "\n"

            views.setTextColor(R.id.kg_meal_widget_day0, Color.BLACK)
            views.setTextColor(R.id.kg_meal_widget_day1, Color.GRAY)
            views.setTextColor(R.id.kg_meal_widget_day2, Color.GRAY)
            views.setViewVisibility(R.id.kg_meal_widget_meals_layout, View.INVISIBLE)
            views.setViewVisibility(R.id.kg_meal_widget_progressbar_layout, View.VISIBLE)
            mealManager!!.getMeal(masterDay, dayOfWeek, mealDay) { md, time, success ->
                if (success) {
                    val mealsText: CharSequence =
                            md!!.data0 + plusText +
                                    md.data1 + plusText +
                                    md.data2 + plusText +
                                    md.data3 + plusText +
                                    md.data4 + plusText +
                                    md.data5

                    views.setViewVisibility(R.id.kg_meal_widget_meals_layout, View.VISIBLE)
                    views.setViewVisibility(R.id.kg_meal_widget_progressbar_layout, View.INVISIBLE)
                    views.setTextViewText(R.id.kg_meal_widget_meals, mealsText)
                    views.setTextViewText(R.id.kg_meal_widget_mealMonthDay, md.mealMonthDay)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MealWidget::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        mealManager = MealManager(context!!)

        val views = RemoteViews(context!!.packageName, R.layout.kg_meal_widget)
        val oCalendar = Calendar.getInstance()
        val dayOfWeek = oCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val plusText = "\n"

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MealWidget::class.java)

        var mealDay = 0

        if (mealDayText0Action.equals(intent!!.action)) {
            mealDay = 1
            views.setTextColor(R.id.kg_meal_widget_day0, Color.BLACK)
            views.setTextColor(R.id.kg_meal_widget_day1, Color.GRAY)
            views.setTextColor(R.id.kg_meal_widget_day2, Color.GRAY)
        } else if (mealDayText1Action.equals((intent!!.action))) {
            mealDay = 3
            views.setTextColor(R.id.kg_meal_widget_day0, Color.GRAY)
            views.setTextColor(R.id.kg_meal_widget_day1, Color.BLACK)
            views.setTextColor(R.id.kg_meal_widget_day2, Color.GRAY)
        } else if (mealDayText2Action.equals(intent!!.action)) {
            mealDay = 5
            views.setTextColor(R.id.kg_meal_widget_day0, Color.GRAY)
            views.setTextColor(R.id.kg_meal_widget_day1, Color.GRAY)
            views.setTextColor(R.id.kg_meal_widget_day2, Color.BLACK)
        }

        views.setViewVisibility(R.id.kg_meal_widget_meals_layout, View.INVISIBLE)
        views.setViewVisibility(R.id.kg_meal_widget_progressbar_layout, View.VISIBLE)
        mealManager!!.getMeal(masterDay, dayOfWeek, mealDay) { md, time, success ->
            if (success) {
                val mealsText: CharSequence =
                        md!!.data0 + plusText +
                                md.data1 + plusText +
                                md.data2 + plusText +
                                md.data3 + plusText +
                                md.data4 + plusText +
                                md.data5

                views.setViewVisibility(R.id.kg_meal_widget_meals_layout, View.VISIBLE)
                views.setViewVisibility(R.id.kg_meal_widget_progressbar_layout, View.INVISIBLE)
                views.setTextViewText(R.id.kg_meal_widget_meals, mealsText)
                views.setTextViewText(R.id.kg_meal_widget_mealMonthDay, md.mealMonthDay)
                appWidgetManager.updateAppWidget(componentName, views)
            }
        }
        appWidgetManager.updateAppWidget(componentName, views)
    }
}