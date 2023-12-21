#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <addons/TokenHelper.h>

#define WIFI_SSID "Carthasisi"
#define WIFI_PASSWD "12345678"

#define FB_API_KEY ""
#define FB_DB_URL ""
#define FB_EMAIL "esp32cam@test.com"
#define FB_PASSWD "passwd123"

// Constants
FirebaseData fbdo;
FirebaseAuth fbauth;
FirebaseConfig fbconfig;

// Private variables
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
bool doing = true;

// Functions declaration
void setupWiFi(void);
void insertRTDB(String params);

void setup() {
  Serial.begin(115200);
  setupWiFi();
  setupFirebase();
}

void loop() {
  if (doing == true){
    insertRTDB("Organik");
    doing = false;
  }
}

void setupWiFi(void) {
  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  while(WiFi.status() != WL_CONNECTED) {
    Serial.println("ERR: Internet connection fail");
    delay(300);
  }
  Serial.println("INFO: Internet connected");
}

void setupFirebase(void) {
  fbconfig.api_key = FB_API_KEY;
  fbconfig.database_url = FB_DB_URL;
  if (Firebase.signUp(&fbconfig, &fbauth, "", "")){
    signupOK = true;
    Serial.println("INFO: SignUp firebase successfully");
  }else{
    Serial.println("ERR: SignUp firebase failed");
  }
  fbconfig.token_status_callback = tokenStatusCallback;
  Firebase.begin(&fbconfig, &fbauth);
  Firebase.reconnectWiFi(true);
} 

void insertRTDB(String params) {
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 60000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    FirebaseJson json;
    json.set("Hasil", params);
    json.set("Ts/.sv", "timestamp");
    
    if(Firebase.RTDB.set(&fbdo, F("/log/"), &json)) {
      Serial.println("INFO: Write log successfully");
    }else{
      Serial.println("ERR: Write log failed");
    }
  }
}
