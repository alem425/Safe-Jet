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
        holder.usernameText.text = "${position + 1}. ${score.username}"
        holder.scoreText.text = score.maxScore.toString()
    }

    override fun getItemCount() = scores.size

    fun updateScores(newScores: List<UserScore>) {
        scores = newScores
        notifyDataSetChanged()
    }
}
