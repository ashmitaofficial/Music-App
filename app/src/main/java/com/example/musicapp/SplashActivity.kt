package com.example.musicapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.home.HomeActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        Handler(Looper.getMainLooper()).postDelayed(
            Runnable
            {
                run {
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeIntent)
                    finish()

                }

            }, 3000
        )


    }
}