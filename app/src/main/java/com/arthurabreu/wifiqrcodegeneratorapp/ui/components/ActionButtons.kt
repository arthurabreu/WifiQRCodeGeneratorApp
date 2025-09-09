package com.arthurabreu.wifiqrcodegeneratorapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@Composable
fun ActionButtons(
    onSave: () -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onSave, shape = RoundedCornerShape(8.dp)) { Text("Salvar Rede") }
        Button(onClick = onGenerate, shape = RoundedCornerShape(8.dp)) { Text("Gerar QR Code") }
    }
}
