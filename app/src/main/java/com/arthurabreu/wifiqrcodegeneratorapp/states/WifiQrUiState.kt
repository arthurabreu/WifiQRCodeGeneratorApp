package com.arthurabreu.wifiqrcodegeneratorapp.states

import android.graphics.Bitmap
import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork

/**
 * UI state for the WiFi QR screen.
 */
data class WifiQrUiState(
    val ssid: String = "",
    val password: String = "",
    val qrCodeBitmap: Bitmap? = null,
    val qrCodeText: String = "",
    val savedNetworks: List<WifiNetwork> = emptyList()
)
