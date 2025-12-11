package com.example.finalproject

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class HomePageViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page_view)

        // 🔥 Get the ImageView that holds your GIF
        val bg = findViewById<ImageView>(R.id.animatedBackground)

        // 🔥 Get the drawable from the ImageView
        val gifDrawable = bg.drawable

        // 🔥 Start the animation if the drawable is an animated GIF
        if (gifDrawable is AnimatedImageDrawable) {
            gifDrawable.start()
        }

        // Buttons
        val startBtn = findViewById<Button>(R.id.startBtn)
        val leaderboardBtn = findViewById<Button>(R.id.leaderboardBtn)

        startBtn.setOnClickListener {
            if (UserManager.isUserLoggedIn(this)) {
                val intent = Intent(this, SelectionActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        leaderboardBtn.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }
    }
}
