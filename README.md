# Wi‑Fi QR Code Generator

Generate and preview QR codes for Wi‑Fi networks. Save your favorite network presets locally and quickly display a scannable QR so any device can connect without typing credentials.

> Built with Kotlin and Jetpack Compose. Uses ZXing for QR generation and Koin for dependency injection.


## Features
- Create QR codes for Wi‑Fi networks
  - SSID, security type (WPA/WPA2, WEP, None), and password
  - Proper Wi‑Fi QR payload format (WIFI:T:...;S:...;P:...;H:...;;)
- Live preview of the QR code as you type
- Save multiple networks locally and reuse them later
- Simple, modern UI with Material 3 and Jetpack Compose
- Lightweight, offline — no network required


## Demo

<!-- Optionally add screenshots or a short GIF of the app in action -->
<!--
<p align="center">
  <img src="docs/screenshot_home.png" width="320" alt="Home screen" />
  <img src="docs/screenshot_qr.png" width="320" alt="QR preview" />
</p>
-->


## Tech Stack
- Language: Kotlin (JVM target 11)
- UI: Jetpack Compose + Material 3
- DI: Koin (android + compose)
- Data: AndroidX DataStore (Preferences) + Gson
- QR: ZXing
- Architecture: MVVM + Use Cases
- Min SDK: 24, Target SDK: 36, Compile SDK: 36


## Architecture Overview
- Domain
  - Model: WifiNetwork
  - Use cases: BuildWifiQRStringUseCase, GenerateQrCodeBitmapUseCase
- Data
  - Repository + Local data source backed by DataStore to persist saved networks
- Presentation
  - ViewModel (Compose-friendly state)
  - Composables for input fields, QR preview, saved list, and actions

File highlights:
- app/src/main/java/com/arthurabreu/wifiqrcodegeneratorapp/domain/usecase/BuildWifiQRStringUseCase.kt
- app/src/main/java/com/arthurabreu/wifiqrcodegeneratorapp/domain/usecase/GenerateQrCodeBitmapUseCase.kt
- app/src/main/java/com/arthurabreu/wifiqrcodegeneratorapp/ui/screens/WifiQrScreen.kt
- app/src/main/java/com/arthurabreu/wifiqrcodegeneratorapp/ui/WifiQrViewModel.kt (or viewmodels/WifiQrViewModel.kt in legacy paths)
- app/src/main/java/com/arthurabreu/wifiqrcodegeneratorapp/di/AppModule.kt


## Getting Started

### Prerequisites
- Android Studio Koala (or newer)
- JDK 11
- Android SDKs for API 24+ (min 24, target 36)

### Clone and Open
```
git clone https://github.com/<your-username>/WifiQRCodeGeneratorApp.git
cd WifiQRCodeGeneratorApp
```
Open the project in Android Studio and let it sync.

### Run
- Select an emulator or a physical device (API 24+)
- Click Run ▶


## Usage
1. Enter your Wi‑Fi SSID
2. Choose the security type (None, WEP, WPA/WPA2)
3. If required, enter the password
4. Tap "Gerar QR Code" to generate/refresh the code
5. Optionally tap "Salvar Rede" to store this preset locally and reuse it later

Scan the shown QR with another device’s camera or Wi‑Fi companion app to connect automatically.


## Permissions
- No dangerous runtime permissions are required for core functionality (QR generation and local persistence).


## Project Scripts and Configuration
- Gradle Kotlin DSL with version catalogs (gradle/libs.versions.toml)
- Compose BOM manages UI dependency versions


## Roadmap
- Share/export QR image
- Dark mode refinements and accessibility improvements
- Optional hidden SSID flag support (H:true)
- Localization (en, pt‑BR, …)


## Contributing
Contributions are welcome! Feel free to:
- Open issues for bugs and feature requests
- Submit pull requests following conventional commits if possible

Basic steps:
- Fork the repo
- Create a feature branch
- Commit with clear messages
- Open a PR with context and screenshots if UI-related


## License
This repository currently does not include a license file. If you intend to open‑source it, consider adding a LICENSE (e.g., MIT or Apache‑2.0) and updating this section accordingly.


## Acknowledgements
- ZXing for QR generation
- AndroidX, Jetpack Compose, and Material 3
- Koin for DI
