# 🏎️ ECHO: Network Telemetry

> **Network Diagnostics, Reimagined.**

![Echo Banner](https://img.shields.io/badge/Status-Active-E10600?style=for-the-badge) ![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-1e1e2e?style=for-the-badge&logo=kotlin) ![License](https://img.shields.io/badge/License-MIT-gray?style=for-the-badge)

**Echo** is a high-performance network diagnostic tool built for Android. It connects you to any domain, checking DNS, TCP, SSL, and HTTP metrics with millisecond precision. Draped in a custom **Catppuccin Mocha + F1 Red** livery, it combines powerful diagnostics with a premium aesthetic.

---

## 📸 Screenshots

<img width="40%" alt="blocked" src="https://github.com/user-attachments/assets/bd56c570-ac40-4d07-9600-afa17c4d6469" /> | <img width="40%" alt="allowed" src="https://github.com/user-attachments/assets/f6278e58-4e00-401c-9251-55f157401ef4" /> |


## 📸 Demo

<div align="center">
  <video src="demo/demo.mp4" width="40%" controls></video>
</div>


---


## 🚀 Features

*   **🔍 Full Telemetry**: Comprehensive analysis of any domain.
*   **🏎️ F1-Inspired UI**: Deep black aesthetics with **Racing Red (#E10600)** accents.
*   **🌐 DNS Analysis**: Resolution time, IP classes, and blocking detection.
*   **🔌 TCP Connectivity**: Port 443/80 connectivity and latency measurement.
*   **🔒 SSL Security**: Certificate validation, issuer checks, and MITM detection.
*   **📡 HTTP Response**: Response headers, status codes, and server info.
*   **📊 Share Report**: Generate clean, developer-friendly text reports.

---

## 🛠️ Tech Stack

Built with modern Android technologies:
*   **Language**: Kotlin
*   **UI**: Jetpack Compose
*   **Theme**: Catppuccin Mocha + F1 Red
*   **Networking**: Java Network APIs (`InetAddress`, `Socket`, `HttpsURLConnection`)
*   **Concurrency**: Kotlin Coroutines & Flows
*   **Architecture**: MVVM with Unidirectional Data Flow

---

## ⚡ Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/echo.git
    cd echo
    ```

2.  **Build the project**:
    ```bash
    ./gradlew assembleDebug
    ```

3.  **Run on device**:
    ```bash
    ./gradlew installDebug
    ```

---

## 🎨 Theme Design

Designed with a focus on speed and clarity.
*   **Base Theme**: Catppuccin Mocha
*   **Accent Color**: F1 Racing Red (`#E10600`)
