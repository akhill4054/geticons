package com.greedygames.geticons.utils

import android.content.Context
import com.greedygames.geticons.BuildConfig

class PreferenceManager private constructor(context: Context) {

    private val _sharedPrefs = context.getSharedPreferences(
        "${BuildConfig.APPLICATION_ID}.PREFS_FILE_KEY",
        Context.MODE_PRIVATE
    )

    fun getIsDarkMode(): Boolean = getBool(KEY_IS_DARK_MODE)

    fun setIsDarkMode(isDarkMode: Boolean) {
        // Update theme modification status
        setWasThemeEverModified()
        // Save dark mode status
        putBool(KEY_IS_DARK_MODE, isDarkMode)
    }

    fun getWasThemeEverModified(): Boolean =
        getBool(KEY_WAS_THEME_EVER_MODIFIED)

    private fun setWasThemeEverModified() {
        putBool(KEY_WAS_THEME_EVER_MODIFIED, true)
    }

    private fun getBool(key: String): Boolean {
        return _sharedPrefs.getBoolean(key, false)
    }

    private fun putBool(key: String, value: Boolean) {
        val editor = _sharedPrefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    companion object {
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_WAS_THEME_EVER_MODIFIED = "was_theme_ever_modified"

        @Volatile
        private var INSTANCE: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            // To reduce number of reads on volatile field.
            var localRef = INSTANCE

            return localRef ?: synchronized(PreferenceManager::class.java) {
                // To reduce number of reads on volatile field.
                localRef = INSTANCE

                // As static field may have been initialized before
                // the lock was acquired.
                return localRef ?: PreferenceManager(context.applicationContext).also {
                    // Init static field.
                    INSTANCE = it
                }
            }
        }
    }
}