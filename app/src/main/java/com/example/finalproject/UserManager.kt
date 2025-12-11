package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREF_NAME = "FinalProjectPrefs"
    private const val KEY_USERNAME = "username"

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

    fun logout(context: Context) {
        getPrefs(context).edit().remove(KEY_USERNAME).apply()
    }
}
