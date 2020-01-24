/**
 *  Copyright © 2019 Clarifai
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.clarifai.clarity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.TextureView
import android.view.WindowManager
import android.widget.Button
import com.clarifai.clarifai_android_sdk.core.Clarifai
import android.support.v7.app.AlertDialog
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.facebook.react.LifecycleState
import com.facebook.react.shell.MainReactPackage
import com.facebook.react.common.ApplicationHolder.getApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import android.app.Activity






/**
 * Home.kt
 * Clarity
 *
 * Copyright © 2018 Clarifai. All rights reserved.
 */

class MyReactActivity : Activity(), DefaultHardwareBackBtnHandler {
    private var mReactRootView: ReactRootView? = null
    private var mReactInstanceManager: ReactInstanceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mReactRootView = ReactRootView(this)
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(application)
                .setCurrentActivity(this)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("index")
                .addPackage(MainReactPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build()
        // The string here (e.g. "MyReactNativeApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        mReactRootView!!.startReactApplication(mReactInstanceManager, "AwesomeReactApp", null)

        setContentView(mReactRootView)
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressed()
    }
}
class Home : AppCompatActivity(), PeriodicPrediction.PredictionTriggers, CameraControl.CameraControlTriggers {
    override fun getCameraPermission() {
        if (!havePermissions()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE_INIT)
            return
        }
    }

    override fun checkCameraPermission(): Boolean {
        return havePermissions()
    }

    private lateinit var captureButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var textureView: TextureView
    private lateinit var outputControl: OutputControl
    private lateinit var dialog: AlertDialog


    private lateinit var cameraControl: CameraControl
    private lateinit var periodicPrediction: PeriodicPrediction

    override fun onCreate(savedInstanceState: Bundle?) {
        val OVERLAY_PERMISSION_REQ_CODE = 1
        super.onCreate(savedInstanceState)
        setupWindow()
        setContentView(R.layout.activity_home)
        findViews()
        loadingScreen(show = true)

        val sharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        val missingKey = getString(R.string.missing_api_key)
        val apiKey = sharedPreferences.getString(getString(R.string.shared_preferences_api_key), missingKey)
        outputControl = OutputControl(this.applicationContext, recyclerView)

        Clarifai.start(this, apiKey)

        if (!havePermissions()) {
            Log.d(TAG, "No permission. So, asking for it")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE_INIT)
            return

        }
        onCreateAfterPermissions()
    }

    override fun onResume() {
        super.onResume()
        if (!havePermissions()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE_RESUME)
            return
        }
        onResumeAfterPermissions()
    }

    override fun onPause() {
        super.onPause()
        periodicPrediction.onPause()
    }

    override fun onStop() {
        super.onStop()
        cameraControl.onStop()
    }

    private fun setupWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun findViews() {
        textureView = findViewById(R.id.texture)
        captureButton = findViewById(R.id.button)
        recyclerView = findViewById(R.id.concepts_list_rv)
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        dialog = builder.create()
    }

    private fun setListeners() {
        captureButton.setOnClickListener {
            // TODO
        }
    }

    private fun loadingScreen(show: Boolean) {
        Log.d(TAG, "Changing the state of loading screen to $show")
        if (show)
            dialog.show()
        else
            dialog.dismiss()
    }

    @SuppressLint("MissingPermission")
    private fun onCreateAfterPermissions() {
        Log.d(TAG, "Have permission from requesting with onCreate")
        cameraControl = CameraControl(textureView, getSystemService(Context.CAMERA_SERVICE) as CameraManager, this)
        periodicPrediction = PeriodicPrediction(this)
        setListeners()
    }

    @SuppressLint("MissingPermission")
    private fun onResumeAfterPermissions() {
        if (periodicPrediction.isModelLoaded) {
            cameraControl.onResume()
            periodicPrediction.onResume()
        }
    }

    private fun havePermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            when (requestCode) {
                CAMERA_REQUEST_CODE_INIT -> onCreateAfterPermissions()
                CAMERA_REQUEST_CODE_RESUME -> onResumeAfterPermissions()
                else -> {
                }
            }
        } catch (se: SecurityException) {
            Log.e(TAG, "Permission not granted")
        }

    }

    override fun onReceivedPredictions(outputs: List<Array<String>>) {
        runOnUiThread {
            if (outputs.isNotEmpty()) {
                outputControl.update(outputs)
            }
        }
    }

    override fun modelLoaded() {
        Log.d(TAG, "Loaded the model")
        runOnUiThread {
            captureButton.isEnabled = true
            captureButton.isClickable = true
            loadingScreen(show = false)
            onResumeAfterPermissions()
        }
    }

    override fun captureBitmap(): Bitmap? {
        if (cameraControl.isCameraSetup()) {
            return cameraControl.bitmap
        }
        return null
    }

    companion object {
        private val TAG = Home::class.java.simpleName
        private const val CAMERA_REQUEST_CODE_INIT = 101
        private const val CAMERA_REQUEST_CODE_RESUME = 102
    }
}
