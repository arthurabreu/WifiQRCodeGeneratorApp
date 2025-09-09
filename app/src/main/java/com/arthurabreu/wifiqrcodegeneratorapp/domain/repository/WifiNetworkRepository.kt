package com.arthurabreu.wifiqrcodegeneratorapp.domain.repository

import com.arthurabreu.wifiqrcodegeneratorapp.domain.model.WifiNetwork

interface WifiNetworkRepository {
    suspend fun getSavedNetworks(): List<WifiNetwork>
    suspend fun saveNetworks(networks: List<WifiNetwork>)
}
