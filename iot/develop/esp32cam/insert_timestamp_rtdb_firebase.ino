#include <Arduino.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// Internet network credentials
#define WIFI_SSID "Carthasis"
#define WIFI_PASSWORD "12345678"

// Firebase setup
#define API_KEY "WEB_API_KEY_PROJECT"
#define DATABASE_URL "RTDB_FIREBASE"
#define USER_EMAIl "EMAIL"
#define USER_PASSWORD "PASSWORD"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
unsigned long count = 0;
bool signupOK = false;

void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  config.token_status_callback = tokenStatusCallback;  // see addons/TokenHelper.h
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    FirebaseJson json;
    json.set("Data", "Hello");
    json.set("Ts/.sv", "timestamp");

    if (Firebase.RTDB.setJSON(&fbdo, "/test/", &json)) {
      Serial.println("PASSED");
    } else {
      Serial.println("ERR: REASON: " + fbdo.errorReason());
    }
  }
}
