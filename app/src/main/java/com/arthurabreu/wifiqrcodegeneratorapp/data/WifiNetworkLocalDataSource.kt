package com.arthurabreu.wifiqrcodegeneratorapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

// Application-scoped Preferences DataStore for persisting networks as a JSON string.
val Context.dataStore by preferencesDataStore(name = "wifi_networks")

/**
 * Local data source backed by Preferences DataStore.
 * We serialize the list of WifiNetwork as JSON using Gson. This keeps the example
 * simple without defining a schema or Room database.
 */
class WifiNetworkLocalDataSource(private val context: Context) {
    private val key = stringPreferencesKey("saved_networks")
    private val gson = Gson()

    suspend fun load(): List<WifiNetwork> {
        val prefs = context.dataStore.data.first()
        val serialized = prefs[key] ?: "[]"
        return runCatching {
            gson.fromJson<List<WifiNetwork>>(serialized, object : TypeToken<List<WifiNetwork>>() {}.type)
        }.getOrDefault(emptyList())
    }

    suspend fun save(networks: List<WifiNetwork>) {
        val serialized = gson.toJson(networks)
        context.dataStore.edit { it[key] = serialized }
    }
}
