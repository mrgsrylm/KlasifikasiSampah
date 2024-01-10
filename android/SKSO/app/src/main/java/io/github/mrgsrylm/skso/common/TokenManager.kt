package io.github.mrgsrylm.skso.common

import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPref: SharedPreferences
) {
    fun saveToken(token: String, expires: Long) {
        sharedPref.edit()
            .putString(Constants.PREF_TOKEN, token)
            .putLong(Constants.PREF_TOKEN_EXP, expires)
            .apply()
    }

    fun getToken(): String? {
        return sharedPref.getString(Constants.PREF_TOKEN, null)
    }

    fun getTokenExpiry(): Long {
        return sharedPref.getLong(Constants.PREF_TOKEN_EXP, 0)
    }

    fun deleteToken() {
        sharedPref.edit().remove(Constants.PREF_TOKEN).apply()
        sharedPref.edit().remove(Constants.PREF_TOKEN_EXP).apply()
    }

    fun isTokenValid(): Boolean {
        val expTime = getTokenExpiry()
        Log.d("TOKEN MANAGER", "Token is valid")
        return (System.currentTimeMillis() / 1000) < expTime
    }

    // private methods
}