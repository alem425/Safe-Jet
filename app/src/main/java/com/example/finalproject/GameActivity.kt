package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Button
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity(), GameModel.GameStateListener {

    private lateinit var gameView: GameView
    private lateinit var gameModel: GameModel
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    // Game loop runnable
    private val gameLoop = object : Runnable {
        override fun run() {
            if (isRunning) {
                // Update game logic
                gameModel.update(System.currentTimeMillis())
                
                // Force redraw
                gameView.invalidate()

                // Schedule next frame (approx 60 FPS)
                handler.postDelayed(this, 16)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lock orientation to portrait programmatically to be safe
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.gameView)
        gameModel = GameModel(this)
        
        // Connect Model and View
        gameView.setGameModel(gameModel)
        gameModel.addListener(this)

        // Setup speed bar
        // Get speed multiplier from shared preferences
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val speedMultiplier = prefs.getFloat("obstacle_speed", 1.0f)
        gameModel.obstacleSpeedMultiplier = speedMultiplier

        // Setup Change Plane button
        val btnChangePlane = findViewById<Button>(R.id.btnChangePlane)
        btnChangePlane.setOnClickListener {
            val intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Retrieve which plane was selected and pass to view
        // Priority: Intent Extra > Saved Preference > Default
        val savedPlane = UserManager.getSelectedPlane(this)
        val selectedPlane = intent.getStringExtra("selectedPlane") ?: savedPlane ?: "plane1"
        gameModel.plane.type = selectedPlane
        gameView.setPlaneType(selectedPlane)

        // Set up touch listener for restart or other interactions if needed
        gameView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (gameModel.gameState == GameState.GAME_OVER) {
                    val action = gameView.handleTouchEvent(event.x, event.y)
                    if (action == GameView.GameAction.LEADERBOARD) {
                         // Go to leaderboard
                         val intent = Intent(this, LeaderboardActivity::class.java)
                         // Maybe pass score?
                         intent.putExtra("score", gameModel.score.total)
                         startActivity(intent)
                         finish() // End current game activity so back button behaves expectedly
                    }
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        gameModel.resumeGame()
        isRunning = true
        handler.post(gameLoop)
    }

    override fun onPause() {
        super.onPause()
        gameModel.pauseGame()
        isRunning = false
        handler.removeCallbacks(gameLoop)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameModel.cleanup()
    }

    // GameStateListener methods
    override fun onScoreUpdated() {}
    override fun onLivesUpdated() {}
    override fun onObstaclesUpdated() {}

    override fun onGameOver(finalScore: Int) {
        // Update High Score in Firebase
        val username = UserManager.getUsername(this)
        if (username != null) {
            FirebaseManager.updateHighScore(username, finalScore)
        }
        
        // Optionally pass score to Leaderboard logic if we were navigating away
        // But for now we stay in GameView and wait for restart tap

    }
}
