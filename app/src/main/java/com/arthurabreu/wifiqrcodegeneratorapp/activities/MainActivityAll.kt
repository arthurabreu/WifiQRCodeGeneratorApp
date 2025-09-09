package com.arthurabreu.wifiqrcodegeneratorapp.activities

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arthurabreu.wifiqrcodegeneratorapp.data.WifiNetworkLocalDataSource
import com.arthurabreu.wifiqrcodegeneratorapp.data.WifiNetworkRepositoryImpl
import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.BuildWifiQRStringUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.GenerateQrCodeBitmapUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.ui.components.NetworkItemCard
import kotlinx.coroutines.launch

/**
 * This file intentionally preserves the "pre-clean" monolithic screen implementation
 * so you can compare it with the current clean version that uses a ViewModel and
 * smaller components. It is NOT referenced anywhere in the app.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiQRCodeGeneratorApp() {
    // Use cases and repository created directly inside the composable
    val buildWifiQRString = remember { BuildWifiQRStringUseCase() }
    val generateQrCode = remember { GenerateQrCodeBitmapUseCase() }

    // UI state held locally in the composable (no ViewModel)
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var qrCodeText by remember { mutableStateOf("") }
    val savedNetworks = remember { mutableStateListOf<WifiNetwork>() }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Repository created directly in the composable and used with coroutines here
    val repository = remember(context) {
        WifiNetworkRepositoryImpl(
            WifiNetworkLocalDataSource(context)
        )
    }

    // Load saved networks when the composable enters composition
    LaunchedEffect(Unit) {
        val list = repository.getSavedNetworks()
        savedNetworks.clear()
        savedNetworks.addAll(list)
    }

    fun saveNetworks() {
        coroutineScope.launch {
            repository.saveNetworks(savedNetworks)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Text(
                    text = "Gerador de QR Code para WiFi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = ssid,
                    onValueChange = { ssid = it },
                    label = { Text("Nome da Rede (SSID)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (ssid.isNotBlank()) {
                                if (savedNetworks.none { it.ssid == ssid }) {
                                    savedNetworks.add(WifiNetwork(ssid, password))
                                    saveNetworks()
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Salvar Rede") }

                    Button(
                        onClick = {
                            val wifiData = buildWifiQRString(ssid, password)
                            qrCodeText = wifiData
                            qrCodeBitmap = generateQrCode(wifiData, 512)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Gerar QR Code") }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            if (savedNetworks.isNotEmpty()) {
                item {
                    Text(
                        text = "Redes Salvas:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(
                    items = savedNetworks,
                    key = { network -> network.ssid + ":" + network.password }
                ) { network ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                savedNetworks.remove(network)
                                saveNetworks()
                                true
                            } else false
                        }
                    )

                    Box(modifier = Modifier.padding(vertical = 4.dp)) {
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            backgroundContent = {
                                val show = dismissState.targetValue != SwipeToDismissBoxValue.Settled
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            if (show) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.background
                                        ),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (show) {
                                        Text(
                                            text = "Excluir",
                                            color = MaterialTheme.colorScheme.onError,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(end = 24.dp)
                                        )
                                    }
                                }
                            },
                            content = {
                                NetworkItemCard(
                                    network = network,
                                    onClick = {
                                        ssid = network.ssid
                                        password = network.password
                                        val wifiData = buildWifiQRString(ssid, password)
                                        qrCodeText = wifiData
                                        qrCodeBitmap = generateQrCode(wifiData, 512)
                                    }
                                )
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                qrCodeBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code para a rede WiFi",
                        modifier = Modifier.size(256.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Conteúdo do QR Code:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = qrCodeText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewAll() {
    // Note: This preview renders the old monolithic composable for comparison only.
    WifiQRCodeGeneratorApp()
}
