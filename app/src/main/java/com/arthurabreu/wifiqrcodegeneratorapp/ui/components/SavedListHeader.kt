package com.arthurabreu.wifiqrcodegeneratorapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.padding

@Composable
fun SavedListHeader(modifier: Modifier = Modifier) {
    Text(
        text = "Redes Salvas:",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = modifier.padding(bottom = 8.dp)
    )
}
