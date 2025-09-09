package com.arthurabreu.wifiqrcodegeneratorapp.domain.usecase

/**
 * Builds the content string that follows the de-facto WiFi QR code format:
 * WIFI:S:<ssid>;T:<auth>;P:<password>;;
 * - S: SSID (network name)
 * - T: Authentication type (e.g., WPA/WEP). We default to WPA.
 * - P: Password
 * The trailing double semicolons terminate the fields.
 */
class BuildWifiQRStringUseCase {
    operator fun invoke(ssid: String, password: String, auth: String = "WPA"): String {
        // Note: in a production app you may need to escape special characters
        // like ';', ',', ':' according to QR spec quirks. We keep it simple here.
        val escapedSsid = ssid.trim()
        val escapedPassword = password
        return "WIFI:S:$escapedSsid;T:$auth;P:$escapedPassword;;"
    }
}
