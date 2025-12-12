package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar

class SelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        val option1 = findViewById<android.widget.ImageButton>(R.id.option1)
        val option2 = findViewById<android.widget.ImageButton>(R.id.option2)
        val option3 = findViewById<android.widget.ImageButton>(R.id.option3)
        // Seek bar stuff
        val speedSeekBar = findViewById<android.widget.SeekBar>(R.id.speedSeekBar)
        val speedLabel = findViewById<android.widget.TextView>(R.id.speedLabel)

        // Load saved speed settings
        val savedSpeed = loadSpeedSettings()
        val progress = ((savedSpeed - 0.5f) / 1.5f * 100).toInt() //converts 0.5-2 into 0-100
        speedSeekBar.progress = progress.coerceIn(0, 100) // Ensure it's within 0-100
        // Update the label to match
        val percentage = (savedSpeed * 100).toInt()
        speedLabel.text = when {
            progress < 25 -> "Slow ($percentage%)"
            progress > 75 -> "Fast ($percentage%)"
            else -> "Normal ($percentage%)"
        }

        //listen for changes
        speedSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Convert progress (0-100) to speed multiplier (0.5x to 2.0x)
                val speedMultiplier = 0.5f + (progress / 100f) * 1.5f

                // Save to shared preferences
                saveSpeedSettings(speedMultiplier)

                // Update label
                val percentage = (speedMultiplier * 100).toInt()
                speedLabel.text = when {
                    progress < 25 -> "Easy ($percentage%)"
                    progress > 75 -> "Hard ($percentage%)"
                    else -> "Normal ($percentage%)"
                }

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                //Not needed
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // Not needed
            }
        })

        option1.setOnClickListener { goToGame("plane_black") }
        option2.setOnClickListener { goToGame("plane_blue") }
        option3.setOnClickListener { goToGame("plane_red") }
    }

    private fun goToGame(selectedPlane: String) {
        UserManager.savePlane(this, selectedPlane)
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("selectedPlane", selectedPlane)
        startActivity(intent)
    }

    private fun saveSpeedSettings(multiplier: Float){
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        prefs.edit().putFloat("obstacle_speed", multiplier).apply()
    }
    private fun loadSpeedSettings(): Float {
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        return prefs.getFloat("obstacle_speed", 1.0f) //default speed of 1.0
    }
}
