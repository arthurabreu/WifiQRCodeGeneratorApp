package com.arthurabreu.wifiqrcodegeneratorapp.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QrPreview(qrCodeBitmap: Bitmap?, qrCodeText: String, modifier: Modifier = Modifier) {
    qrCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code para a rede WiFi",
            modifier = modifier.size(256.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Conteúdo do QR Code:", fontWeight = FontWeight.SemiBold)
        Text(text = qrCodeText, fontSize = 12.sp)
    }
}
