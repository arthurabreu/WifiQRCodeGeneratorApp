package com.arthurabreu.wifiqrcodegeneratorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arthurabreu.wifiqrcodegeneratorapp.ui.screens.WifiQrScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WifiQrAppRoot() }
    }
}

@Composable
fun WifiQrAppRoot() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) { WifiQrScreen() }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WifiQrAppRoot()
}
