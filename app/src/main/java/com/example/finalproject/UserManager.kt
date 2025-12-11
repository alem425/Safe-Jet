package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREF_NAME = "FinalProjectPrefs"
    private const val KEY_USERNAME = "username"


    private const val KEY_SELECTED_PLANE = "selected_plane"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return getUsername(context) != null
    }

    fun getUsername(context: Context): String? {
        return getPrefs(context).getString(KEY_USERNAME, null)
    }

    fun saveUser(context: Context, username: String) {
        getPrefs(context).edit().putString(KEY_USERNAME, username).apply()
    }
    

    fun getSelectedPlane(context: Context): String? {
        return getPrefs(context).getString(KEY_SELECTED_PLANE, null)
    }

    fun savePlane(context: Context, plane: String) {
        getPrefs(context).edit().putString(KEY_SELECTED_PLANE, plane).apply()
    }

    fun logout(context: Context) {
        getPrefs(context).edit().remove(KEY_USERNAME).remove(KEY_SELECTED_PLANE).remove(KEY_HAS_RATED).remove(KEY_RATING).apply()
    }

    // Rating System
    private const val KEY_HAS_RATED = "has_rated"
    private const val KEY_RATING = "user_rating"

    fun hasRated(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_HAS_RATED, false)
    }

    fun getRating(context: Context): Int {
        return getPrefs(context).getInt(KEY_RATING, 0)
    }

    fun saveRating(context: Context, rating: Int) {
        getPrefs(context).edit()
            .putBoolean(KEY_HAS_RATED, true)
            .putInt(KEY_RATING, rating)
            .apply()
    }
}
