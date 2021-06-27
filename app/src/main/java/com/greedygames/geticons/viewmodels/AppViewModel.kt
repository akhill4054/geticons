package com.greedygames.geticons.viewmodels

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.greedygames.geticons.utils.PreferenceManager

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    private val prefs = PreferenceManager.getInstance(application)

    init {
        if (prefs.getWasThemeEverModified()) {
            val isDarkMode = prefs.getIsDarkMode()

            // Notify observers
            _isDarkMode.value = isDarkMode

            // Change app theme forcefully
            setTheme(isDarkMode)
        } else {
            // Get system theme
            val currentNightMode =
                application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            _isDarkMode.value = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun setTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun toggleDarkMode() {
        val isDarkMode = !_isDarkMode.value!!

        // Update prefs
        prefs.setIsDarkMode(isDarkMode)

        // Update status
        _isDarkMode.value = isDarkMode

        // Change app theme forcefully
        setTheme(isDarkMode)
    }
}