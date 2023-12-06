#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "Carthasis";
const char* password =  "12345678";

void setup() {

  Serial.begin(115200);
  delay(4000);   //Delay needed before calling the WiFi.begin

  WiFi.begin(ssid, password); 

  while (WiFi.status() != WL_CONNECTED) { //Check for the connection
    delay(1000);
    Serial.println("Connecting to WiFi..");
  }
  Serial.println("Connected to the WiFi network");
}

void loop() {
  if ((WiFi.status() == WL_CONNECTED)) { //Check the current connection status

    HTTPClient http;

    http.begin("https://reqbin.com/echo/get/json"); //Specify the URL
    int httpCode = http.GET();                                       

    if (httpCode > 0) { //Check for the returning code

        String payload = http.getString();
        Serial.println(httpCode);
        Serial.println(payload);
      }

    else {
      Serial.println("Error on HTTP request");
    }

    http.end(); //Free the resources
  }

  delay(1000000);
}
