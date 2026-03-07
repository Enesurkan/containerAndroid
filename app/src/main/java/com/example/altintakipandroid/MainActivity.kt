package com.example.altintakipandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altintakipandroid.ui.AppGate
import com.example.altintakipandroid.ui.AppGateViewModel
import com.example.altintakipandroid.ui.theme.AltintakipAndroidTheme
import com.example.altintakipandroid.ui.theme.SurfaceCream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AltintakipAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SurfaceCream
                ) {
                    val viewModel: AppGateViewModel = viewModel()
                    AppGate(viewModel = viewModel)
                }
            }
        }
    }
}
