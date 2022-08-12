package com.wallstick.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AppPref(context: Context) {
    fun getInt(key: String?, defValue: Int): Int {
        return appSharedPref.getInt(key, defValue)
    }

    fun setInt(key: String?, value: Int) {
        prefEditor.putInt(key, value).apply()
    }

    fun clearInt(key: String?) {
        setInt(key, 0)
    }

    fun getString(key: String?): String? {
        return appSharedPref.getString(key, "")
    }

    fun setString(key: String?, value: String?) {
        prefEditor.putString(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return appSharedPref.getBoolean(key, defValue)
    }

    fun setBoolean(key: String?, value: Boolean) {
        prefEditor.putBoolean(key, value).apply()
    }

    fun clearString(key: String?) {
        setString(key, "")
    }

    companion object {
        const val WALL_STICK_DATA = "WALL_STICK_DATA"
        lateinit var appSharedPref: SharedPreferences
        lateinit var prefEditor: SharedPreferences.Editor
        const val LATEST_CURRENT_PAGE = "LATEST_CURRENT_PAGE"
        const val LATEST_CURRENT_PER_PAGE = "LATEST_CURRENT_PER_PAGE"
        const val IS_FIRST_OPEN = "IS_FIRST_OPEN"
        const val TRENDING_CURRENT_PAGE = "TRENDING_CURRENT_PAGE"
        const val TRENDING_CURRENT_PER_PAGE = "TRENDING_CURRENT_PER_PAGE"
    }

    init {
        appSharedPref = context.getSharedPreferences(WALL_STICK_DATA, Activity.MODE_PRIVATE)
        prefEditor = appSharedPref.edit()
    }
}
