package com.arthurabreu.wifiqrcodegeneratorapp.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arthurabreu.wifiqrcodegeneratorapp.data.WifiNetworkLocalDataSource
import com.arthurabreu.wifiqrcodegeneratorapp.data.WifiNetworkRepositoryImpl
import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork
import com.arthurabreu.wifiqrcodegeneratorapp.domain.repository.WifiNetworkRepository
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.BuildWifiQRStringUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.GenerateQrCodeBitmapUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * UI state for the WiFi QR screen.
 * This single immutable data holder makes the UI a pure function of state.
 */
data class WifiQrUiState(
    val ssid: String = "",
    val password: String = "",
    val qrCodeBitmap: Bitmap? = null,
    val qrCodeText: String = "",
    val savedNetworks: List<WifiNetwork> = emptyList()
)

/**
 * ViewModel for the WiFi QR screen.
 *
 * Dependency injection style: manual.
 * - The dependencies (Repository and UseCases) are provided via constructor.
 * - A custom Factory (below) knows how to build those dependencies.
 * - No DI framework (Hilt/Koin) is used; this keeps wiring explicit and testable.
 */
class WifiQrViewModel(
    private val repository: WifiNetworkRepository,
    private val buildWifiQRString: BuildWifiQRStringUseCase,
    private val generateQrCode: GenerateQrCodeBitmapUseCase
) : ViewModel() {

    // Backing StateFlow that the UI collects from.
    private val _state = MutableStateFlow(WifiQrUiState())
    val state: StateFlow<WifiQrUiState> = _state.asStateFlow()

    init {
        // Load persisted networks at start (runs once per VM lifecycle).
        loadSavedNetworks()
    }

    // --- UI intents / state reducers ---
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

    // --- Data loading / persistence ---
    private fun loadSavedNetworks() {
        viewModelScope.launch {
            // Offload IO to Dispatchers.IO to avoid blocking the main thread.
            val list = withContext(Dispatchers.IO) { repository.getSavedNetworks() }
            _state.value = _state.value.copy(savedNetworks = list)
        }
    }

    private fun persistSavedNetworks(list: List<WifiNetwork>) {
        // Write on IO dispatcher; no need to switch back as UI observes the flow.
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNetworks(list)
        }
    }

    /**
     * Manual DI Factory: centralizes construction of the ViewModel and its dependencies.
     * The Activity passes its Application instance so we can create the local data source
     * that relies on Android context.
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repo = WifiNetworkRepositoryImpl(WifiNetworkLocalDataSource(application))
            return WifiQrViewModel(
                repository = repo,
                buildWifiQRString = BuildWifiQRStringUseCase(),
                generateQrCode = GenerateQrCodeBitmapUseCase()
            ) as T
        }
    }
}
