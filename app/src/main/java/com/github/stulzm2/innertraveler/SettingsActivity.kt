package com.github.stulzm2.innertraveler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        isChecked()

        theme_simple_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utility.setTheme(applicationContext, 2, true)
                recreateActivity()
            } else {
                Utility.setTheme(applicationContext, 1, false)
                recreateActivity()
            }
        }
    }

    private fun isChecked() {
        if (Utility.getDarkTheme()) {
            theme_simple_switch.isChecked = true
        }
    }

    private fun recreateActivity() {
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}
