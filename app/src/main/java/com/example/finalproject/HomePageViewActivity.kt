package com.example.finalproject

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.LinearLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class HomePageViewActivity : AppCompatActivity() {
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page_view)

        MobileAds.initialize(this) {}

        val adLayout = findViewById<LinearLayout>(R.id.adContainer)

        adView = AdView(this)
        val adSize: AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)
        val adUnitId = "ca-app-pub-3940256099942544/6300978111" // test banner id
        adView.adUnitId = adUnitId

        val builder = AdRequest.Builder()
        // optional keywords like in professor example
        builder.addKeyword("game").addKeyword("jet")
        val adRequest: AdRequest = builder.build()

        adLayout.addView(adView)
        adView.loadAd(adRequest)

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
                val savedPlane = UserManager.getSelectedPlane(this)
                if (savedPlane != null) {
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("selectedPlane", savedPlane)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, SelectionActivity::class.java)
                    startActivity(intent)
                }
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
    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }
}
