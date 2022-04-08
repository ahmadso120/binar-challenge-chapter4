package com.sopian.challenge4.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.sopian.challenge4.R
import com.sopian.challenge4.model.LoggedInUser
import com.sopian.challenge4.model.RegisteredUser

object AppLocalData {

    private const val KEY_USERNAME = "USERNAME"
    private const val KEY_EMAIL = "EMAIL"
    private const val KEY_PASSWORD = "PASSWORD"
    private const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"

    private fun getSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.resources.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

    fun setUserLoggedIn(context: Context, loggedInUser: LoggedInUser) {
        getSharedPreference(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, loggedInUser.isLoggedIn)
            putString(KEY_USERNAME, loggedInUser.username)
            apply()
        }
    }

    fun getUserLoggedIn(context: Context): LoggedInUser? {
        val isLoggedIn = getSharedPreference(context).getBoolean(KEY_IS_LOGGED_IN, false)
        val username = getSharedPreference(context).getString(KEY_USERNAME, "")

        if (isLoggedIn) {
            return username?.let {
                LoggedInUser(isLoggedIn, it)
            }
        }

        return null
    }

    fun dropUserLoggedIn(context: Context) {
        getSharedPreference(context).edit().apply {
            remove(KEY_IS_LOGGED_IN)
            apply()
        }
    }

    fun setRegisteredUser(context: Context, registeredUser: RegisteredUser) {
        getSharedPreference(context).edit().apply {
            putString(KEY_USERNAME, registeredUser.username)
            putString(KEY_EMAIL, registeredUser.email)
            putString(KEY_PASSWORD, registeredUser.password)
            apply()
        }
    }

    fun getRegisteredUser(context: Context): RegisteredUser? {
        val username = getSharedPreference(context).getString(KEY_USERNAME, "")
        val email = getSharedPreference(context).getString(KEY_EMAIL, "")
        val password = getSharedPreference(context).getString(KEY_PASSWORD, "")

        if (!username.isNullOrEmpty() &&
            !email.isNullOrEmpty() &&
            !password.isNullOrEmpty()) {
            return RegisteredUser(username,email,password)
        }

        return null
    }

}