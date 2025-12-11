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
        scoreTextView.text = "Score: $score"

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = LeaderboardAdapter(emptyList())
        recyclerView.adapter = adapter

        // Fetch top 10 scores
        FirebaseManager.getLeaderboard(10) { scores ->
            adapter.updateScores(scores)
        }
    }
}
