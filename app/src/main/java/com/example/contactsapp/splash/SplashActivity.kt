package com.example.contactsapp.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.contactsapp.*

class SplashActivity : AppCompatActivity() {
    private var firstStart = false
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        firstStart = prefs.getBoolean("firstStart", true)
        // asking for permissions
        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
        )
        if (!hasPermissions(this, *PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)

        if (hasPerm(Manifest.permission.READ_CONTACTS) and hasPerm(Manifest.permission.CALL_PHONE)) {
            moveNext(firstStart)
        }
        val editor = prefs.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


    private fun moveNext(firstStart: Boolean) {

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
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            var contactPermission = false
            var phonePermission = false
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                contactPermission = true
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                phonePermission = true
            if (contactPermission and phonePermission) {
                Handler(Looper.myLooper()!!).postDelayed({
                    val nextIntent = if (firstStart) {

                        // move to start page because this the first time login
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
    }

}