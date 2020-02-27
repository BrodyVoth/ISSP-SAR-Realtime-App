package com.clarifai.clarity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar

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
}
