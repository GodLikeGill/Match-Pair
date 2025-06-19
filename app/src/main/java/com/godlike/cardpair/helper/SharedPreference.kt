package com.godlike.cardpair.helper

import android.content.Context

fun saveToPreferences(context: Context, key: String, value: Int) {
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt(key, value)
        apply()
    }
}

fun readFromPreferences(context: Context, key: String, defaultValue: Int = 0): Int {
    val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    return sharedPref.getInt(key, defaultValue)
}
