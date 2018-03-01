package com.github.stulzm2.innertraveler

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by matthewstulz on 2/28/18.
 */
object Utility {

    private var darkTheme : Boolean = false

    fun setTheme(context: Context, theme: Int, boolean: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putInt(context.getString(R.string.prefs_theme_key), theme).apply()
        darkTheme = boolean
    }

    fun getTheme(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(context.getString(R.string.prefs_theme_key), -1)
    }

    fun getDarkTheme() : Boolean {
        if (darkTheme)
            return true
        return false
    }

    fun setDarkTheme(boolean: Boolean) {
        darkTheme = boolean
    }
}