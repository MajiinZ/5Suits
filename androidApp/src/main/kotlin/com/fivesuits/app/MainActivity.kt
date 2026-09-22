package com.fivesuits.app

import android.os.Bundle
import com.fivesuits.app.data.LocalStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalStore.initialize(applicationContext)
        enableEdgeToEdge()
        setContent { App() }
    }
}
