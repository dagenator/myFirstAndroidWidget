package com.example.myfirstandroidwidget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlin.math.log


class WidgetProvider : AppWidgetProvider() {

    private val ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE"
    private var counter = 0


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        println(Integer.toString(counter))

        appWidgetIds.forEach { appWidgetId ->


            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = ACTION_SIMPLEAPPWIDGET

            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Get the layout for the widget and attach an on-click listener
            // to the button.
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.xml.app_widget_info
            ).apply {
                setOnClickPendingIntent(R.id.widgetButton, pendingIntent)
            }
            views.setTextViewText(R.id.widgetText, Integer.toString(counter))
            // Tell the AppWidgetManager to perform an update on the current
            // widget.
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }


    override fun onReceive(context: Context, intent: Intent?) {

        super.onReceive(context, intent)
        counter++
        println(Integer.toString(counter))
        println(intent?.action)
        val views = RemoteViews(context.packageName, R.xml.app_widget_info)
        views.setTextViewText(R.id.widgetText, Integer.toString(counter))
        val appWidget = ComponentName(context, WidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidget, views);

    }

}