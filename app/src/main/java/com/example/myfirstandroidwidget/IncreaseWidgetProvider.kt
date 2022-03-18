package com.example.myfirstandroidwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

class IncreaseWidgetProvider : AppWidgetProvider() {

    companion object Counter {
        private var counter = 0
    }

    private val ACTION_BROADCAST_INCREASE = "ACTION_BROADCAST_INCREASE"

    private fun getIntent(context: Context, action: String, appWidgetId: Int): Intent {
        val intent = Intent(context, IncreaseWidgetProvider::class.java)
        intent.action = action
        intent.putExtra("id", appWidgetId)
        return intent
    }

    private fun getRemoteViews(
        context: Context, pendingIntent: PendingIntent
    ): RemoteViews {

        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.widget
        ).apply {
            setOnClickPendingIntent(R.id.widgetButton, pendingIntent)
        }
        return views
    }

    private fun getPendingIntent(
        context: Context,
        intent: Intent,
        appWidgetId: Int
    ): PendingIntent {
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                context, appWidgetId, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        return pendingIntent
    }


    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = getIntent(context, ACTION_BROADCAST_INCREASE, appWidgetId)
        val pendingIntent = getPendingIntent(context, intent, appWidgetId)
        val views = getRemoteViews(context, pendingIntent)
        views.setTextViewText(R.id.widgetText, counter.toString())
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        println(counter.toString())

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {

        super.onReceive(context, intent)

        if (intent != null) {
            when (intent.action) {
                ACTION_BROADCAST_INCREASE -> {
                    counter++

                    val appWidget = intent.getIntExtra("id", 0)
                    val pendingIntent: PendingIntent = getPendingIntent(context, intent, appWidget)
                    val views: RemoteViews = getRemoteViews(context, pendingIntent)
                    views.setTextViewText(R.id.widgetText, counter.toString())

                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    appWidgetManager.updateAppWidget(appWidget, views)
                }
            }
        }
    }
}