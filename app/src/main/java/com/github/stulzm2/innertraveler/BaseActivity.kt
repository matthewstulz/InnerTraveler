package com.github.stulzm2.innertraveler

import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

open class BaseActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTheme()
    }

    private fun updateTheme() {
        if (Utility.getTheme(applicationContext) <= THEME_LIGHT) {
            setTheme(R.style.AppTheme_Light)
            Utility.setDarkTheme(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark_light)

            }
        } else if (Utility.getTheme(applicationContext) == THEME_BLACK) {
            setTheme(R.style.AppTheme_Black)
            Utility.setDarkTheme(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorDark_black)
            }
        }
    }

    companion object {
        private const val THEME_LIGHT = 1
        private const val THEME_BLACK = 2
    }
}