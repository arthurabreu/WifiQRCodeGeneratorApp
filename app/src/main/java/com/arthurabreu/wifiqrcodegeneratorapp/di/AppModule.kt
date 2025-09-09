package com.arthurabreu.wifiqrcodegeneratorapp.di

import com.arthurabreu.wifiqrcodegeneratorapp.data.WifiNetworkLocalDataSource
import com.arthurabreu.wifiqrcodegeneratorapp.data.WifiNetworkRepositoryImpl
import com.arthurabreu.wifiqrcodegeneratorapp.domain.repository.WifiNetworkRepository
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.BuildWifiQRStringUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase.GenerateQrCodeBitmapUseCase
import com.arthurabreu.wifiqrcodegeneratorapp.viewmodels.WifiQrViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Data source requires Android context
    single { WifiNetworkLocalDataSource(androidContext()) }

    // Repository
    single<WifiNetworkRepository> { WifiNetworkRepositoryImpl(get()) }

    // Use cases
    factory { BuildWifiQRStringUseCase() }
    factory { GenerateQrCodeBitmapUseCase() }

    // ViewModel
    viewModel { WifiQrViewModel(get(), get(), get()) }
}
