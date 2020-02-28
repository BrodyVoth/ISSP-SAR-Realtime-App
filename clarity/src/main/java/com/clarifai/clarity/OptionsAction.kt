package com.clarifai.clarity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.*
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import java.util.prefs.PreferenceChangeListener

class OptionsAction : AppCompatActivity() {


    private lateinit var toolbar: android.support.v7.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_options_action)

        toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        toolbar.setTitle("settings")

        fragmentManager.beginTransaction().add(R.id.fragment_container, SettingsFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.gallery -> {
            Toast.makeText(this, "Gallery Clicked", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.interval -> {
            Toast.makeText(this, "Interval Clicked", Toast.LENGTH_SHORT).show()
            true
        }

        R.id.home -> {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            true

        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragment() {

        private lateinit var sharedPreferences: SharedPreferences
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.options_menu)

            bindPreferenceSummaryToValue(findPreference(getString(R.string.adjust_time_interval)))


        }

        override fun onPreferenceTreeClick(preferenceScreen: PreferenceScreen?, preference: Preference?): Boolean {
            return when (preference?.key) {
                getString(R.string.adjust_time_interval) -> {
                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return true
                    val defaultValue = 5000
                    PeriodicPrediction.REFRESH_RATE_MS = sharedPref.getInt(getString(R.string.adjust_time_interval), defaultValue)
                    true
                }
                else -> {
                    super.onPreferenceTreeClick(preferenceScreen, preference)
                }
            }
        }


    }

    companion object {
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.context).getString(preference.key, ""))
        }


        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            val stringValue = newValue.toString()
            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(stringValue)
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)
            } else {
                preference.summary = stringValue
            }
            true
        }
    }
}
