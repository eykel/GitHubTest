package com.s2start.githubtest.util

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences(context: Context) {

    private val mPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.APP.NAME, Context.MODE_PRIVATE)

    fun store(key: String, value: String) {
        mPreferences.edit().putString(key, value).apply()
    }
    fun store(key: String, value: Boolean) {
        mPreferences.edit().putBoolean(key, value).apply()
    }
    fun store(key: String, value: Int) {
        mPreferences.edit().putInt(key, value).apply()
    }

    fun remove(key: String) {
        mPreferences.edit().remove(key).apply()
    }

    fun get(key: String, type: String): String {
        return mPreferences.getString(key, "") ?: ""
    }
    fun get(key: String, type: Boolean): Boolean {
        return mPreferences.getBoolean(key, type)
    }
    fun get(key: String, type: Int): Int {
        return mPreferences.getInt(key, 0)
    }

}