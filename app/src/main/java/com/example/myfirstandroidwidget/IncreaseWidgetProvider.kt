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
    private val colorRandomizer = ColorRandomizer()

    companion object Counter {
        private var counter = 0

        private const val INCREASE_ID_KEY = "id"
        private const val DEFAULT_WIDGET_ID = 0
        private const val ACTION_BROADCAST_INCREASE = "ACTION_BROADCAST_INCREASE"
        private const val ACTION_BROADCAST_COLOR_CHANGE = "ACTION_BROADCAST_COLOR_CHANGE"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
            WidgetSaver.save(appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidget = intent?.getIntExtra(INCREASE_ID_KEY, DEFAULT_WIDGET_ID) ?: return

        val increasePendingIntent: PendingIntent = context.getPendingIntent(
            ACTION_BROADCAST_INCREASE,
            appWidget
        )

        val colorPendingIntent: PendingIntent = context.getPendingIntent(
            ACTION_BROADCAST_COLOR_CHANGE,
            appWidget
        )

        val views: RemoteViews = context.getRemoteViews(increasePendingIntent, colorPendingIntent)

        handleIntentAction(intent, views, appWidget, context, appWidgetManager)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val increasePendingIntent: PendingIntent = context.getPendingIntent(
            ACTION_BROADCAST_INCREASE,
            appWidgetId
        )

        val colorPendingIntent: PendingIntent = context.getPendingIntent(
            ACTION_BROADCAST_COLOR_CHANGE,
            appWidgetId
        )

        val views: RemoteViews = context.getRemoteViews(increasePendingIntent, colorPendingIntent)

        views.setTextViewText(R.id.widgetText, counter.toString())

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun Context.getPendingIntent(
        action: String,
        appWidgetId: Int
    ): PendingIntent {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT

        val intent = getIntent(this, action, appWidgetId)

        return PendingIntent.getBroadcast(this, appWidgetId, intent, flag)
    }

    private fun getIntent(context: Context, action: String, appWidgetId: Int): Intent =
        Intent(context, this::class.java).apply {
            this.action = action
            putExtra(INCREASE_ID_KEY, appWidgetId)
        }

    private fun Context.getRemoteViews(
        IncreasePendingIntent: PendingIntent,
        ColorPendingIntent: PendingIntent
    ): RemoteViews = RemoteViews(
        this.packageName,
        R.layout.widget
    ).apply {
        setOnClickPendingIntent(R.id.widgetIncreaseButton, IncreasePendingIntent)
        setOnClickPendingIntent(R.id.widgetBackgroundButton, ColorPendingIntent)
    }

    private fun handleIntentAction(
        intent: Intent,
        views: RemoteViews,
        appWidget: Int,
        context: Context,
        appWidgetManager: AppWidgetManager
    ) {
        when (intent.action) {
            ACTION_BROADCAST_INCREASE -> {
                counter++
                views.setTextViewText(R.id.widgetText, counter.toString())
                WidgetSaver.save(appWidget)
                onUpdate(context, appWidgetManager, WidgetSaver.widgets.toIntArray())
            }
            ACTION_BROADCAST_COLOR_CHANGE -> {
                val randomColor = colorRandomizer.getColor()
                views.setInt(
                    R.id.widget, "setBackgroundColor",
                    randomColor
                )
                appWidgetManager.updateAppWidget(appWidget, views)
            }
        }
    }
}