package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(private var scores: List<UserScore>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameText: TextView = view.findViewById(R.id.usernameText)
        val scoreText: TextView = view.findViewById(R.id.scoreText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = scores[position]
        var medal = ""
        if (position == 0) {
            medal = "🥇 "
        } else if (position == 1) {
            medal = "🥈 "
        } else if (position == 2) {
            medal = "🥉 "
        }

        holder.usernameText.text = "$medal${position + 1}. ${score.username}"
        holder.scoreText.text = score.maxScore.toString()
        val currentUsername = UserManager.getUsername(holder.itemView.context)

        if (score.username == currentUsername) {
            holder.usernameText.setTypeface(holder.usernameText.typeface, android.graphics.Typeface.BOLD)
            holder.scoreText.setTypeface(holder.scoreText.typeface, android.graphics.Typeface.BOLD)
        } else {
            holder.usernameText.setTypeface(holder.usernameText.typeface, android.graphics.Typeface.NORMAL)
            holder.scoreText.setTypeface(holder.scoreText.typeface, android.graphics.Typeface.NORMAL)
        }
    }

    override fun getItemCount() = scores.size

    fun updateScores(newScores: List<UserScore>) {
        scores = newScores
        notifyDataSetChanged()
    }
}
