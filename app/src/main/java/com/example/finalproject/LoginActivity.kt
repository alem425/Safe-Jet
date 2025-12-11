package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val confirmBtn = findViewById<Button>(R.id.confirmBtn)
        val errorText = findViewById<TextView>(R.id.errorText)

        confirmBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()

            if (username.isEmpty()) {
                errorText.text = "Please enter a username"
                errorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Check if user exists in Firebase
            FirebaseManager.checkUsernameExists(username) { exists ->
                if (exists) {
                    errorText.text = "Username currently taken, please choose another"
                    errorText.visibility = View.VISIBLE
                } else {
                    // Create user in Firebase
                    FirebaseManager.createUser(username) { success ->
                        if (success) {
                            // Save locally
                            UserManager.saveUser(this, username)

                            // Navigate to Selection
                            val intent = Intent(this, SelectionActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            errorText.text = "Error creating user. Check connection."
                            errorText.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}
