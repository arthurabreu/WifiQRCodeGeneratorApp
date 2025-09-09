package com.arthurabreu.wifiqrcodegeneratorapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurabreu.wifiqrcodegeneratorapp.ui.components.*
import com.arthurabreu.wifiqrcodegeneratorapp.ui.components.NetworkItemCard
import com.arthurabreu.wifiqrcodegeneratorapp.viewmodels.WifiQrViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiQrScreen(viewModel: WifiQrViewModel = koinViewModel()) {
    val initialState = remember { viewModel.state.value }
    val state = produceState(initialValue = initialState, key1 = viewModel) {
        viewModel.state.collect { value = it }
    }.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item { HeaderTitle() }

        item {
            NetworkFields(
                ssid = state.ssid,
                password = state.password,
                onSsidChange = viewModel::onSsidChange,
                onPasswordChange = viewModel::onPasswordChange
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ActionButtons(
                onSave = { viewModel.saveCurrentNetworkIfNeeded() },
                onGenerate = { viewModel.generateQr() }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        if (state.savedNetworks.isNotEmpty()) {
            item { SavedListHeader() }

            items(
                items = state.savedNetworks,
                key = { network -> network.ssid + ":" + network.password }
            ) { network ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            viewModel.deleteNetwork(network)
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
                                onClick = { viewModel.selectNetwork(network) }
                            )
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item { QrPreview(qrCodeBitmap = state.qrCodeBitmap, qrCodeText = state.qrCodeText) }
    }
}
