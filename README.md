# 🏎️ ECHO: Network Telemetry

> **"If you're not fast, you're last."** — *Network Diagnostics, Reimagined.*

![Echo Banner](https://img.shields.io/badge/Status-Lights%20Out-E10600?style=for-the-badge) ![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-1e1e2e?style=for-the-badge&logo=kotlin) ![License](https://img.shields.io/badge/License-MIT-gray?style=for-the-badge)

**Echo** is a high-performance network diagnostic tool built for Android. It connects you to the grid, checking DNS, TCP, SSL, and HTTP metrics with millisecond precision. Draped in a custom **Catppuccin Mocha + F1 Red** livery, it looks as fast as it works.

---

## 🏁 Pole Position (Features)

*   **🔍 Full Telemetry**: Comprehensive analysis of any domain.
*   **🏎️ F1-Inspired UI**: Deep black aesthetics with **Racing Red (#E10600)** accents.
*   **🌐 DNS Sector Times**: Resolution time, IP classes, and blocking detection.
*   **🔌 TCP Grip Check**: Port 443/80 connectivity and latency measurement.
*   **🔒 SSL Safety Car**: Certificate validation, issuer checks, and MITM detection.
*   **📡 HTTP DRS**: Response headers, status codes, and server info.
*   **📊 Share Report**: Generate clean, developer-friendly text reports for your team.

---

## 🛠️ Pit Crew (Tech Stack)

Built on the latest chassis:
*   **Engine**: Kotlin + Jetpack Compose
*   **Livery**: Catppuccin Mocha Theme (modified)
*   **Telemetry**: Java Network APIs (`InetAddress`, `Socket`, `HttpsURLConnection`)
*   **Comms**: Kotlin Coroutines & Flows
*   **Architecture**: MVVM with Unidirectional Data Flow

---

## 🚦 Lights Out (Getting Started)

1.  **Qualifying**:
    Clone the repository to your local paddock.
    ```bash
    git clone https://github.com/your-username/echo.git
    cd echo
    ```

2.  **Pit Stop**:
    Build the project using Gradle.
    ```bash
    ./gradlew assembleDebug
    ```

3.  **Race Day**:
    Deploy to your device.
    ```bash
    ./gradlew installDebug
    ```

---

## 📸 Race Highlights

| Dashboard | Diagnostics |
| :---: | :---: |
| *Top Bar with animated stripe* | *Detailed status cards* |
| *Minimal status indicators* | *Carbon-fiber style inputs* |

---

## 🏆 Podium

Designed with speed and aesthetics in mind.
*   **Base Theme**: Catppuccin Mocha
*   **Accent**: F1 Racing Red

> *Stay flat out.* 🏎️💨
