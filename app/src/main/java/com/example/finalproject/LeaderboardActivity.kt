package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LeaderboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val score = intent.getIntExtra("score", 0)
        val scoreTextView = findViewById<TextView>(R.id.finalScore)
        scoreTextView.text = score.toString()
    }
}
