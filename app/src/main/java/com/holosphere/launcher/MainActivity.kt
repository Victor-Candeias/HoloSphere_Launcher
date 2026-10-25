package com.holosphere.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.holosphere.launcher.feature.home.HomeScreen
import com.holosphere.launcher.feature.home.HomeViewModel
import com.holosphere.launcher.feature.home.HomeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val container = (application as HoloSphereApplication).container
            MaterialTheme {
                Surface { HomeScreen(HomeViewModelFactory(container.appsRepository)) }
            }
        }
    }
}
