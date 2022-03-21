package com.example.myfirstandroidwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import java.util.*

class IncreaseWidgetProvider : AppWidgetProvider() {

    companion object Counter {
        private var counter = 0
        private var list: MutableList<Int> = mutableListOf()
    }

    private val ACTION_BROADCAST_INCREASE = "ACTION_BROADCAST_INCREASE"
    private val ACTION_BROADCAST_COLOR_CHANGE = "ACTION_BROADCAST_COLOR_CHANGE"

    private fun getIntent(context: Context, action: String, appWidgetId: Int): Intent {
        val intent = Intent(context, IncreaseWidgetProvider::class.java)
        intent.action = action
        return intent.putExtra("id", appWidgetId)
    }

    private fun getRemoteViews(
        context: Context, IncreasePendingIntent: PendingIntent, ColorPendingIntent: PendingIntent
    ): RemoteViews {

        return RemoteViews(
            context.packageName,
            R.layout.widget
        ).apply {
            setOnClickPendingIntent(R.id.widgetIncreaseButton, IncreasePendingIntent)
            setOnClickPendingIntent(R.id.widgetBackgroundButton, ColorPendingIntent)
        }
    }

    private fun getPendingIntent(
        context: Context,
        intent: Intent,
        appWidgetId: Int
    ): PendingIntent {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, appWidgetId, intent, flag)
    }


    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val increasePendingIntent = getPendingIntent(
            context,
            getIntent(context, ACTION_BROADCAST_INCREASE, appWidgetId),
            appWidgetId
        )
        val colorPendingIntent = getPendingIntent(
            context,
            getIntent(context, ACTION_BROADCAST_COLOR_CHANGE, appWidgetId),
            appWidgetId
        )
        val views = getRemoteViews(context, increasePendingIntent, colorPendingIntent)
        views.setTextViewText(R.id.widgetText, counter.toString())
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
            saveWidgetId(appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {

        super.onReceive(context, intent)

        if (intent != null) {
            val appWidget = intent.getIntExtra("id", 0)

            val increasePendingIntent: PendingIntent =
                getPendingIntent(
                    context,
                    getIntent(context, ACTION_BROADCAST_INCREASE, appWidget),
                    appWidget
                )

            val colorPendingIntent: PendingIntent =
                getPendingIntent(
                    context,
                    getIntent(context, ACTION_BROADCAST_COLOR_CHANGE, appWidget),
                    appWidget
                )

            val views: RemoteViews =
                getRemoteViews(context, increasePendingIntent, colorPendingIntent)

            when (intent.action) {
                ACTION_BROADCAST_INCREASE -> {
                    counter++
                    views.setTextViewText(R.id.widgetText, counter.toString())
                    saveWidgetId(appWidget)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    onUpdate(context, appWidgetManager, list.toIntArray())
                }
                ACTION_BROADCAST_COLOR_CHANGE -> {
                    val rnd = Random()
                    val color: Int =
                        Color.argb(180, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                    views.setInt(
                        R.id.widget, "setBackgroundColor",
                        color
                    )
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    appWidgetManager.updateAppWidget(appWidget, views)
                }
            }
        }
    }

    private fun saveWidgetId(widgetId: Int) {
        if (!list.contains(widgetId)) {
            list.add(widgetId)
        }
    }
}