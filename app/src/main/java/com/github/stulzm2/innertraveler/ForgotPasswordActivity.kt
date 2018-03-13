package com.github.stulzm2.innertraveler

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {

    private val TAG = "ForgotPasswordActivity"
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Password"
        initOperations()
    }

    private fun initOperations() {
        mAuth = FirebaseAuth.getInstance()
        btn_submit!!.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = et_forgot_password?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val message = "Email sent."
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    updateUI()
                } else {
                    Toast.makeText(this, "No user found with this email.",
                            Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}