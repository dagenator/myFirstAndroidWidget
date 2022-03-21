package com.example.myfirstandroidwidget

import android.graphics.Color
import java.util.*

class ColorRandomizer {
    private val random = Random()

    fun getColor(): Int = Color.argb(
        180,
        random.nextInt(256),
        random.nextInt(256),
        random.nextInt(256)
    )
}