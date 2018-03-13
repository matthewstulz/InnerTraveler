package com.github.stulzm2.innertraveler

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var email: String? = null
    private var password: String? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initOperations()
    }

    private fun initOperations() {
        mAuth = FirebaseAuth.getInstance()
        tv_forgot_password!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity,
                        ForgotPasswordActivity::class.java)) }
        btn_sign_up!!
                .setOnClickListener { startActivity(Intent(this@LoginActivity,
                        SignUpActivity::class.java)) }
        btn_login!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = et_email?.text.toString()
        password = et_password?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.visibility = View.VISIBLE

            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                    progressBar.visibility = View.INVISIBLE
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
