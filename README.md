# RF Peak Power & VSWR Calculator (SJU & STT)

A specialized Android application for radio frequency (RF) radar engineers and air traffic control (ATC) electronics technicians to monitor, calculate, and log **Peak Power Output** and **VSWR (Voltage Standing Wave Ratio)** across redundant transmitter channels.

Designed specifically for the radar and transmitter facilities serving:
- **San Juan (SJU)**: Luis Muñoz Marín International Airport (TJSJ)
- **Saint Thomas (STT)**: Cyril E. King Airport (TIST)

---

## 📲 Download Android APK

Download the latest installable Android APK directly from the **[GitHub Releases](https://github.com/drosadocastro-bit/Radar-Transmitter-RF-Power-and-VSWR/releases/latest)** page.

### Installing on an Android Device
1. Open the **[Latest Release](https://github.com/drosadocastro-bit/Radar-Transmitter-RF-Power-and-VSWR/releases/latest)** page on your Android phone.
2. Under **Assets**, tap the `.apk` file to download it.
3. Tap the downloaded file and enable **Install unknown apps** if prompted, then tap **Install**.
   - **Minimum requirement:** Android 7.0 (API 24) or higher.

---

## 📡 Overview & Purpose

Field technicians perform periodic maintenance and calibration on high-power directional couplers and power sensors. This app replaces manual calculation sheets and clipboards with an intuitive, field-ready mobile console that:
1. Compares **New** field measurements directly against documented **Baseline** calibrations.
2. Automatically calculates **Peak Power Output** (in MW and kW) and **VSWR** in real time.
3. Automatically converts between power meter readings in milliwatts (**mW**) and decibels (**dBm**).
4. Tracks maintenance history with local on-device logging, notes, and report sharing.

---

## 🛠️ Key Features

- **Facility Toggle (SJU & STT)**: Instant animated switching between San Juan and St. Thomas radar telemetry parameters.
- **Dual-Channel Support (CH A & CH B)**: Full side-by-side management for redundant primary and secondary transmitter channels.
- **Bidirectional Unit Synchronization**: Enter forward power in **mW** to auto-calculate **dBm**, or type **dBm** to auto-calculate **mW** ($10 \cdot \log_{10}(\text{mW})$).
- **Live Output Diagnostics**:
  - **Peak Power Output**: Computes total forward power in Megawatts (MW) and Kilowatts (kW), displaying delta ($\Delta$) against baseline.
  - **VSWR & Return Loss**: Live calculation of reflection coefficient ($\Gamma$), Return Loss ($RL$ in dB), and VSWR with color-coded safety badges (Excellent, Normal, Acceptable, High).
- **Interactive Visual Gauge**: Custom Compose Canvas analog-style gauge displaying the VSWR needle across standard safety thresholds ($1.0$ to $2.0+$).
- **Math Breakdown Modal**: Step-by-step breakdown displaying exact equations and substituted numerical values.
- **PDF Reference Sheet**: In-app digital view of the original calibration spreadsheet table for quick bench reference.
- **Inspection History & Telemetry Export**: Save inspection logs with technician name and notes using an offline-first Room database; export and share formatted text reports via Android Share Sheet.

---

## 🧮 Mathematical Formulas

The app implements standard microwave transmission line and directional coupler mathematics:

### 1. Forward Coupled Power
$$P_{\text{fwd (dBm)}} = C_{\text{inc (dB)}} + P_{\text{meter, fwd (dB)}}$$

### 2. Average Power & Peak Power Output
$$P_{\text{avg (W)}} = 10^{\frac{P_{\text{fwd (dBm)}} - 30}{10}}$$
$$P_{\text{peak (MW)}} = \frac{P_{\text{avg (W)}}}{\text{DutyFactor}}, \quad P_{\text{peak (kW)}} = P_{\text{peak (MW)}} \times 1000$$
*(Default duty factors: $\approx 619.71$ for SJU, $\approx 625.80$ for STT)*

### 3. Reflected Power & Return Loss
$$P_{\text{rev (dBm)}} = C_{\text{rev (dB)}} - |P_{\text{meter, rev (dB)}}| + \text{PadOffset}$$
$$\text{Return Loss } RL_{\text{(dB)}} = P_{\text{fwd (dBm)}} - P_{\text{rev (dBm)}}$$

### 4. Reflection Coefficient ($\Gamma$) & VSWR
$$\Gamma = 10^{-\frac{RL}{20}}$$
$$\text{VSWR} = \frac{1 + \Gamma}{1 - \Gamma}$$

---

## 📊 Calibrated Baseline Reference

| Site | Channel | Incident Coupler | Rev Coupler | Fwd Meter (mW) | Fwd Meter (dB) | Rev Meter (\|dB\|) | Peak Power | VSWR |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **SJU** | **CH A** | 54.18 dB | 44.87 dB | 1.81 mW | 2.55 dB | 9.55 dB | **0.76 MW** | **1.21** |
| **SJU** | **CH B** | 54.71 dB | 44.77 dB | 1.72 mW | 2.35 dB | 9.55 dB | **0.82 MW** | **1.20** |
| **STT** | **CH A** | 54.00 dB | 45.10 dB | 1.92 mW | 2.83 dB | 29.53 dB | **0.77 MW** | **1.10** |
| **STT** | **CH B** | 54.42 dB | 44.52 dB | 1.78 mW | 2.52 dB | 5.50 dB | **0.79 MW** | **1.25** |

---

## 🏗️ Architecture & Tech Stack

- **Language**: Kotlin 2.x
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) with unidirectional data flow
  - `RfCalculatorViewModel`: Dedicated formula calculation engine with reactive `StateFlow`s
  - `MainViewModel`: Orchestrates UI state, facility selection, dialogs, and persistence
- **Local Database**: Room Database with Kotlin Symbol Processing (KSP) for offline persistence
- **Graphics**: Custom hardware-accelerated Compose Canvas for the dynamic VSWR gauge
- **Async**: Kotlin Coroutines & `StateFlow` / `Flow`

---

## 🚀 Building & Running

### Requirements
- Android Studio Ladybug (or newer)
- Android SDK 36 (Minimum SDK: 24)
- JDK 11+

### Build from Terminal
```bash
# Build debug APK
gradle :app:assembleDebug

# Build signed release APK (requires my-upload-key.jks in project root)
STORE_PASSWORD="<your-password>" KEY_PASSWORD="<your-password>" gradle :app:assembleRelease

# Run unit tests
gradle :app:testDebugUnitTest
```

### Publishing a Downloadable APK via GitHub Actions
The repository includes [`.github/workflows/release-apk.yml`](.github/workflows/release-apk.yml) to automatically build and publish downloadable APKs to GitHub Releases:

1. *(Recommended for signed release builds)* Add these **Repository Secrets** in GitHub (**Settings → Secrets and variables → Actions**):
   - `KEYSTORE_BASE64`: Base64-encoded contents of `my-upload-key.jks`
   - `STORE_PASSWORD`: Keystore password
   - `KEY_PASSWORD`: Key password
   *(If these secrets are not configured, the workflow automatically falls back to building a signed debug APK.)*
2. Push a version tag (e.g., `git tag v2.0 && git push origin v2.0`), or trigger **Build & Release APK** manually from the **Actions** tab with a `release_tag` such as `v2.0`.

---

## 📄 License
Internal technical engineering tool for radar transmitter maintenance and telemetry calibration.
