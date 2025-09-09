package com.arthurabreu.wifiqrcodegeneratorapp.activities

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.arthurabreu.wifiqrcodegeneratorapp.ui.WifiQrViewModel
import com.arthurabreu.wifiqrcodegeneratorapp.ui.components.NetworkItemCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WifiQrAppRoot()
        }
    }
}

@Composable
fun WifiQrAppRoot() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        WifiQrScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiQrScreen() {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    // This is effectively manual dependency injection:
    // - We explicitly create a ViewModelProvider with a custom Factory that knows
    //   how to construct WifiQrViewModel and its dependencies (Repository + UseCases).
    // - No DI framework (like Hilt/Koin) is used here; the graph is wired by hand,
    //   keeping creation at the composition entry point so the same ViewModel instance
    //   is retained across recompositions thanks to remember(activity) and the Activity
    //   being the ViewModelStoreOwner.
    val viewModel = remember(activity) {
        ViewModelProvider(
            activity,
            WifiQrViewModel.Factory(activity.application)
        )[WifiQrViewModel::class.java]
    }
    // Collect StateFlow from the ViewModel in a Compose-friendly way:
    // - We snapshot the current state as initialValue to avoid a null/blank frame.
    // - produceState launches a coroutine tied to this composition, collecting
    //   viewModel.state and updating 'value' whenever the flow emits.
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
                // Swipe-to-dismiss confirmation:
                // - We only allow EndToStart (right-to-left) as a destructive action.
                // - When the target value becomes EndToStart, we delete the item and
                //   return true to finalize the dismissal animation.
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

        item {
            QrPreview(qrCodeBitmap = state.qrCodeBitmap, qrCodeText = state.qrCodeText)
        }
    }
}

@Composable
private fun HeaderTitle() {
    Text(
        text = "Gerador de QR Code para WiFi",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkFields(
    ssid: String,
    password: String,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    OutlinedTextField(
        value = ssid,
        onValueChange = onSsidChange,
        label = { Text("Nome da Rede (SSID)") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Senha") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ActionButtons(
    onSave: () -> Unit,
    onGenerate: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onSave, shape = RoundedCornerShape(8.dp)) { Text("Salvar Rede") }
        Button(onClick = onGenerate, shape = RoundedCornerShape(8.dp)) { Text("Gerar QR Code") }
    }
}

@Composable
private fun SavedListHeader() {
    Text(
        text = "Redes Salvas:",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun QrPreview(qrCodeBitmap: Bitmap?, qrCodeText: String) {
    qrCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code para a rede WiFi",
            modifier = Modifier.size(256.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Conteúdo do QR Code:", fontWeight = FontWeight.SemiBold)
        Text(text = qrCodeText, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WifiQrAppRoot()
}