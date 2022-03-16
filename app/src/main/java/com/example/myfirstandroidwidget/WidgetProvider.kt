package com.example.myfirstandroidwidget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

object Info {
    var counter = 0
}

class WidgetProvider : AppWidgetProvider() {

    private val ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE"

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = ACTION_SIMPLEAPPWIDGET
        intent.putExtra("id", appWidgetId)


        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Get the layout for the widget and attach an on-click listener
        // to the button.
        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.widget
        ).apply {
            setOnClickPendingIntent(  R.id.widgetButton, pendingIntent)
        }
        views.setTextViewText(R.id.widgetText, Integer.toString(Info.counter))
        // Tell the AppWidgetManager to perform an update on the current
        // widget.
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        println(Integer.toString(Info.counter))

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {

        super.onReceive(context, intent)
        Info.counter++
        println(Integer.toString(Info.counter))
        println(intent?.action)

        val appWidget = intent?.getIntExtra("id", 0)

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, appWidget!!, intent!!,
            PendingIntent.FLAG_IMMUTABLE
        )


        val views: RemoteViews = RemoteViews(
            context.packageName,
            R.layout.widget
        ).apply {
            setOnClickPendingIntent(  R.id.widgetButton, pendingIntent)
        }
        views.setTextViewText(R.id.widgetText, Integer.toString(Info.counter))


        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidget!!, views)

    }
}