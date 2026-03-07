package com.example.altintakipandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altintakipandroid.ui.AppGate
import com.example.altintakipandroid.ui.AppGateViewModel
import com.example.altintakipandroid.ui.theme.AltintakipAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppGateViewModel = viewModel()
            val state by viewModel.state.collectAsState()
            val themeStyle = state.config.theme

            AltintakipAndroidTheme(themeStyle = themeStyle) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AppGate(viewModel = viewModel)
                }
            }
        }
    }
}
