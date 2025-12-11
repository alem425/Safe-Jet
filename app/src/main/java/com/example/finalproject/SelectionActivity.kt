package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        val option1 = findViewById<Button>(R.id.option1)
        val option2 = findViewById<Button>(R.id.option2)
        val option3 = findViewById<Button>(R.id.option3)

        option1.setOnClickListener { goToGame("plane1") }
        option2.setOnClickListener { goToGame("plane2") }
        option3.setOnClickListener { goToGame("plane3") }
    }

    private fun goToGame(selectedPlane: String) {
        UserManager.savePlane(this, selectedPlane)
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("selectedPlane", selectedPlane)
        startActivity(intent)
    }
}
