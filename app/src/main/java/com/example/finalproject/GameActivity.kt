package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Retrieve which plane was selected
        val selectedPlane = intent.getStringExtra("selectedPlane")

        // Show the plane chosen on the screen
        val planeText = findViewById<TextView>(R.id.planeChosen)
        planeText.text = "Selected Plane: $selectedPlane"

        // Example: after game finishes, send a fake score
        val score = 15 // placeholder number

        // Update High Score in Firebase
        val username = UserManager.getUsername(this)
        if (username != null) {
            FirebaseManager.updateHighScore(username, score)
        }

        // Send the score to the Leaderboard screen
        val intent = Intent(this, LeaderboardActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
    }
}
