package com.example.myfirstandroidwidget

object WidgetSaver {
    private val _widgets: MutableList<Int> = mutableListOf()
    val widgets: List<Int> get() = _widgets


    fun save(widgetId: Int) = _widgets
        .takeIf { it.contains(widgetId).not() }
        ?.add(widgetId)
}