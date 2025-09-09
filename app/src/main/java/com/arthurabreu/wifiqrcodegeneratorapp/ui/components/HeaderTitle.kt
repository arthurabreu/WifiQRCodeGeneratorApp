package com.arthurabreu.wifiqrcodegeneratorapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier.Companion
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp

@Composable
fun HeaderTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Gerador de QR Code para WiFi",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(bottom = 16.dp)
    )
}
