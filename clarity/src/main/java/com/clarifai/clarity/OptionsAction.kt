package com.clarifai.clarity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.*
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import java.util.prefs.PreferenceChangeListener
import android.preference.PreferenceFragment
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import com.clarifai.clarifai_android_sdk.utils.Log
import java.lang.reflect.Array

class OptionsAction : AppCompatActivity() {


    private lateinit var toolbar: android.support.v7.widget.Toolbar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_options_action)
        
//        setupSharedPreferences()

        toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        toolbar.setTitle("settings")
        createRadioButtons()

        val savedInterval = getInterval(this)
        Toast.makeText(this, "Saved Interval: " + savedInterval, Toast.LENGTH_SHORT).show()


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

    private lateinit var group: RadioGroup
    private lateinit var button: RadioButton
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private fun createRadioButtons() {

        group = findViewById(R.id.radio_group_intervals)
        val intervalOptions: IntArray = resources.getIntArray(R.array.picture_intervals)
        val intervalStrings = resources.getStringArray(R.array.time_intervals)

        for(i in 0 until intervalOptions.size) {
            val intervals = intervalOptions[i]
            val strings = intervalStrings[i]

            button = RadioButton(this)

            button.setText(strings)


            button.setOnClickListener(View.OnClickListener {
                Toast.makeText(this, "You clicked " + strings, Toast.LENGTH_SHORT).show()
                saveIntervalChange(intervals)
            })

            group.addView(button)


            if (intervals == getInterval(this)) {
                button.isChecked
            }
        }




    }
    private fun saveIntervalChange(interval: Int){
        prefs = this.getSharedPreferences("IntervalPrefs", Context.MODE_PRIVATE)
        editor = prefs.edit()
        editor.putInt("Picture Intervals", interval)
        editor.apply()
    }

    fun getInterval(context: Context): Int {
        prefs = context.getSharedPreferences("IntervalPrefs", Context.MODE_PRIVATE)
        return prefs.getInt("Picture Intervals", 0)
    }




//        private lateinit var sharedPreferences: SharedPreferences
//
//        private fun setupSharedPreferences() {
//            sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE)
//        }

//        private fun loadInterval(sharedPreferences: SharedPreferences) {
//            Log.d("Interval",sharedPreferences.getString(getString(R.string.adjust_time_interval),getString(R.string.pref_5000_interval_value)))
//            changeIntervalTime(sharedPreferences.getString(getString(R.string.adjust_time_interval),getString(R.string.pref_5000_interval_value)))
//        }

//        private fun changeIntervalTime(time_intervals_values: String?) {
//            Log.d("Interval", time_intervals_values)
//            if (time_intervals_values.equals("5000")) {
//                PeriodicPrediction.REFRESH_RATE_MS = 5000
//            } else if(time_intervals_values.equals("10000")) {
//                PeriodicPrediction.REFRESH_RATE_MS = 10000
//            } else {
//                PeriodicPrediction.REFRESH_RATE_MS = 15000
//            }
//        }
    }

