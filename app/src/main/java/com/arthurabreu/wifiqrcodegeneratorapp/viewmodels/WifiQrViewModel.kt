package com.arthurabreu.wifiqrcodegeneratorapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork
import com.arthurabreu.wifiqrcodegeneratorapp.domain.repository.WifiNetworkRepository
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.BuildWifiQRStringUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.GenerateQrCodeBitmapUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.states.WifiQrUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WifiQrViewModel(
    private val repository: WifiNetworkRepository,
    private val buildWifiQRString: BuildWifiQRStringUseCase,
    private val generateQrCode: GenerateQrCodeBitmapUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WifiQrUiState())
    val state: StateFlow<WifiQrUiState> = _state.asStateFlow()

    init { loadSavedNetworks() }

    fun onSsidChange(value: String) {
        _state.value = _state.value.copy(ssid = value)
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    fun generateQr() {
        val current = _state.value
        val wifiData = buildWifiQRString(current.ssid, current.password)
        val bitmap = generateQrCode(wifiData, 512)
        _state.value = current.copy(qrCodeText = wifiData, qrCodeBitmap = bitmap)
    }

    fun selectNetwork(network: WifiNetwork) {
        val wifiData = buildWifiQRString(network.ssid, network.password)
        val bitmap = generateQrCode(wifiData, 512)
        _state.value = _state.value.copy(
            ssid = network.ssid,
            password = network.password,
            qrCodeText = wifiData,
            qrCodeBitmap = bitmap
        )
    }

    fun saveCurrentNetworkIfNeeded() {
        val current = _state.value
        if (current.ssid.isBlank()) return
        if (current.savedNetworks.any { it.ssid == current.ssid }) return
        val updated = current.savedNetworks + WifiNetwork(current.ssid, current.password)
        _state.value = current.copy(savedNetworks = updated)
        persistSavedNetworks(updated)
    }

    fun deleteNetwork(network: WifiNetwork) {
        val updated = _state.value.savedNetworks.filterNot { it.ssid == network.ssid && it.password == network.password }
        _state.value = _state.value.copy(savedNetworks = updated)
        persistSavedNetworks(updated)
    }

    private fun loadSavedNetworks() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { repository.getSavedNetworks() }
            _state.value = _state.value.copy(savedNetworks = list)
        }
    }

    private fun persistSavedNetworks(list: List<WifiNetwork>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNetworks(list)
        }
    }
}
