package com.example.contactsapp.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.contactsapp.MainActivity
import com.example.contactsapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)
        Handler(Looper.myLooper()!!).postDelayed({
            val nextIntent = if (firstStart) {
                // move to start page because this the first time login
                val editor = prefs.edit()
                editor.putBoolean("firstStart", false)
                editor.apply()
                Intent(this@SplashActivity, LoginActivity::class.java)
            } else {
                // it's not the first time login
                Intent(this@SplashActivity, MainActivity::class.java)
            }
            startActivity(nextIntent)
            finish()
        }, 2000)
    }
}