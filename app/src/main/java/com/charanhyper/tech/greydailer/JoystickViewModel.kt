package com.charanhyper.tech.greydailer

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class JoystickViewModel(app: Application) : AndroidViewModel(app) {

    var latitude by mutableStateOf("37.7749")
        private set
    var longitude by mutableStateOf("-122.4194")
        private set
    var isMocking by mutableStateOf(false)
        private set
    var speedKmh by mutableStateOf("15.0") // Speed preset slider (Walk: 5, Run: 15, Cycle: 30, Car: 60)
    var errorMessage by mutableStateOf("")
        private set

    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                MockLocationService.BROADCAST_STATUS -> {
                    intent.getStringExtra(MockLocationService.EXTRA_ERROR)?.let { msg ->
                        errorMessage = msg
                        isMocking = false
                    }
                }
                MockLocationService.BROADCAST_JOYSTICK_POSITION -> {
                    val lat = intent.getDoubleExtra(MockLocationService.EXTRA_LAT, 0.0)
                    val lng = intent.getDoubleExtra(MockLocationService.EXTRA_LNG, 0.0)
                    latitude = "%.6f".format(lat)
                    longitude = "%.6f".format(lng)
                    isMocking = true // If we receive location ticks, service is active
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(MockLocationService.BROADCAST_STATUS)
            addAction(MockLocationService.BROADCAST_JOYSTICK_POSITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            app.registerReceiver(statusReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            app.registerReceiver(statusReceiver, filter)
        }

        // Restore if service is already running
        val prefs = app.getSharedPreferences("mock_location_service_state", Context.MODE_PRIVATE)
        val currentMode = prefs.getString("mode", null)
        if (currentMode == "joystick") {
            isMocking = true
            val savedLat = prefs.getString("joystick_lat", null)?.toDoubleOrNull() ?: 37.7749
            val savedLng = prefs.getString("joystick_lng", null)?.toDoubleOrNull() ?: -122.4194
            latitude = "%.6f".format(savedLat)
            longitude = "%.6f".format(savedLng)
        }
    }

    fun onLatLngPicked(lat: Double, lng: Double) {
        latitude = "%.6f".format(lat)
        longitude = "%.6f".format(lng)
    }

    fun clearError() {
        errorMessage = ""
    }

    fun startJoystickMocking() {
        val lat = latitude.toDoubleOrNull() ?: run {
            errorMessage = "Invalid latitude"
            return
        }
        val lng = longitude.toDoubleOrNull() ?: run {
            errorMessage = "Invalid longitude"
            return
        }
        errorMessage = ""
        val intent = Intent(getApplication(), MockLocationService::class.java).apply {
            action = MockLocationService.ACTION_JOYSTICK_START
            putExtra(MockLocationService.EXTRA_LAT, lat)
            putExtra(MockLocationService.EXTRA_LNG, lng)
        }
        getApplication<Application>().startForegroundService(intent)
        isMocking = true
    }

    fun updateJoystickVector(bearing: Float, speedMps: Float) {
        if (!isMocking) return
        val intent = Intent(getApplication(), MockLocationService::class.java).apply {
            action = MockLocationService.ACTION_JOYSTICK_UPDATE
            putExtra(MockLocationService.EXTRA_BEARING, bearing)
            putExtra(MockLocationService.EXTRA_SPEED_MPS, speedMps)
        }
        getApplication<Application>().startService(intent)
    }

    fun stopJoystickMocking() {
        val intent = Intent(getApplication(), MockLocationService::class.java).apply {
            action = MockLocationService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
        isMocking = false
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(statusReceiver)
        } catch (_: Exception) {}
    }
}
