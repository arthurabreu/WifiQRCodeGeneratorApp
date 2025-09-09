package com.arthurabreu.wifiqrcodegeneratorapp.data

import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork
import com.arthurabreu.wifiqrcodegeneratorapp.domain.repository.WifiNetworkRepository

/**
 * Simple Repository implementation delegating to a local data source.
 * This layer allows us to swap implementations (e.g., remote API) without
 * touching the ViewModel or UI.
 */
class WifiNetworkRepositoryImpl(private val local: WifiNetworkLocalDataSource) : WifiNetworkRepository {
    override suspend fun getSavedNetworks(): List<WifiNetwork> = local.load()
    override suspend fun saveNetworks(networks: List<WifiNetwork>) = local.save(networks)
}
