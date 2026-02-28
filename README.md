📱 Secure GUI Authenticator

A desktop JavaFX application that generates real-time TOTP codes for multiple accounts, similar to Google Authenticator.

Supports multiple accounts

Auto-refreshes OTP every 30 seconds

Shows countdown timer for each OTP

Uses RFC 6238 TOTP standard (HMAC-SHA1, 6-digit codes)

Cross-platform (Linux, Windows, macOS with Java 17+ and JavaFX)

🛠 Features

Add multiple accounts with account name and Base32 secret

Auto-generate 6-digit OTP codes

Real-time countdown display

Live update every second

Easy to extend: encryption, QR codes, persistent storage

⚙ Requirements

Java 17+ (OpenJDK recommended)

JavaFX 17+ (controls module)

On Linux (Pop!_OS / Ubuntu):

sudo apt update
sudo apt install openjdk-17-jdk openjfx
🚀 How to Run
1️⃣ Compile:
javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml AuthenticatorApp.java
2️⃣ Run:
java --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml AuthenticatorApp
3️⃣ Optional: Run with Script

Create a file named run.sh:

#!/bin/bash
javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml AuthenticatorApp.java
java --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml AuthenticatorApp

Make it executable and run:

chmod +x run.sh
./run.sh
📝 Usage

Enter Account Name (e.g., Gmail, GitHub)

Enter Base32 Secret Key (from authenticator setup)

Click Add Account

See OTP code and countdown timer appear

Repeat to add multiple accounts

🔒 Security Notes

OTP codes are generated using TOTP RFC 6238 standard

Secrets are not encrypted yet (plan to add AES encryption)

Never share your Base32 secrets

💡 Future Enhancements

Encrypt stored secrets (AES)

Save accounts persistently

Generate/scan QR codes for easy setup

Master password protection

Cross-platform installer

📂 Project Structure
Authenticator/
├── AuthenticatorApp.java   # Main JavaFX TOTP application
├── README.md               # This file
└── run.sh                  # Optional script to compile & run
⚡ References

RFC 6238 TOTP Standard: https://www.rfc-editor.org/rfc/rfc6238

Google Authenticator: https://github.com/google/google-authenticator

JavaFX Documentation: https://openjfx.io/