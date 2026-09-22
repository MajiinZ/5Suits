package com.fivesuits.app.data

import android.content.Context
import android.content.SharedPreferences

actual class LocalStore {
    private val preferences: SharedPreferences
        get() = checkNotNull(applicationContext) { "Initialize LocalStore before creating App." }
            .getSharedPreferences("five_suits", Context.MODE_PRIVATE)

    actual fun read(key: String): String? = preferences.getString(key, null)
    actual fun write(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }
    actual fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    companion object {
        private var applicationContext: Context? = null
        fun initialize(context: Context) {
            applicationContext = context.applicationContext
        }
    }
}
