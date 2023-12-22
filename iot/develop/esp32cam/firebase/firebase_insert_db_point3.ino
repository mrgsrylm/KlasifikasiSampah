#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <HTTPClient.h>
#include <addons/TokenHelper.h>

#define WIFI_SSID = "Carthasis"
#define WIFI_PASSWD = "12345678"
#define FB_API_KEY = ""
#define FB_PROJECT_ID = ""
#define FB_EMAIL = ""
#define FB_PASSWD = ""

FirebaseData fbdo;
FirebaseAuth fbauth;
FirebaseConfig fbconfig;

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
bool doing = true;


void setupWiFi(void);
void setupFirebase(void);
void insertFirestore(String params);

void setup() {
  Serial.begin(115200);
  setupWiFi();
  setupFirebase();
}

void loop() {
  if(doing == true) {
    insertFirestore("Organik");
    doing = false;
  }
}

void setupWiFi(void) {
  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  while(WiFi.status() != WL_CONNECTED){
    Serial.println("ERR: Internet connection fail");
    delay(300);
  }
  Serial.println("INFO: WiFi configured");
}

void setupFirebase(void) {
  fbconfig.api_key = FB_API_KEY;
  if(!Firebase.signUp(&fbconfig, &fbauth, "", "")){
    Serial.println("ERR: SignUp firebase fail");
  }
  signupOK = true;
  Serial.println("INFO: Firebase configured");
  Firebase.reconnectWiFi(true);
  Firebase.begin(&fbconfig, &fbauth);
}

void insertFirestore(String params, String photo) {
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 60000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    String documentPath = "logs/(default)/doc_id";

    HTTPClient http;
    String timeString;
    http.begin("http://worldtimeapi.org/api/timezone/Asia/Jakarta");
    int httpCode = http.GET();  
    if (httpCode < 0) {
      timeString = "Failed to set time";
    } 
    timeString = http.getString();
    http.end();

    // TODO parse timeString and save as map on firebase
    FirebaseJson json;
    json.set("fields/classified_at/stringValue", timeString);
    json.set("fields/photo/stringValue", photo)
    json.set("fields/result/stringValue", params);

    if (!Firebase.Firestore.createDocument(&fbdo, FB_PROJECT_ID, "", documentPath.c_str(), json.raw())) {
      Serial.println("ERR: Write log failed");
    }

    Serial.println("INFO: Write log successfully");
  }
}
