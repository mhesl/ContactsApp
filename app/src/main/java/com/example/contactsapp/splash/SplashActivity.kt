package com.example.contactsapp.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.contactsapp.MainActivity
import com.example.contactsapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)
        // asking for permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                1
            )
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!(requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission required to read contacts", Toast.LENGTH_LONG).show()
        }
    }
}