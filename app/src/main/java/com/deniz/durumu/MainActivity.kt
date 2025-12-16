package com.deniz.durumu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.deniz.durumu.ui.SeaConditionScreen
import com.deniz.durumu.ui.theme.DenizDurumuTheme
import com.deniz.durumu.ui.theme.LightGray

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DenizDurumuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LightGray
                ) {
                    SeaConditionScreen()
                }
            }
        }
    }
}
