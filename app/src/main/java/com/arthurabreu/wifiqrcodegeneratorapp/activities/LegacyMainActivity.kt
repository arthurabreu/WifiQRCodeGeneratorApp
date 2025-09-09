package com.arthurabreu.wifiqrcodegeneratorapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/**
 * Legacy launcher Activity to run the monolithic version for comparison.
 * This uses WifiQRCodeGeneratorApp() defined in MainActivityAll.kt.
 */
class LegacyMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiQRCodeGeneratorApp()
        }
    }
}
