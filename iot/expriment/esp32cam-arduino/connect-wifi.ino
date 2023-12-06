#include <WiFi.h>

const char* SSID = "Carthasis";
const char* PASSWD = "12345678";


void setup() {
  Serial.begin(115200);
  WiFi.begin(SSID, PASSWD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }

  Serial.println("Connected to WiFi");
}

void loop() {
}

