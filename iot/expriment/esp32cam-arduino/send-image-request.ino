#include <WiFi.h>
#include "esp_camera.h"
#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"
#include <base64.h>
#include <HTTPClient.h>

// CAMERA_MODEL_AI_THINKER
#define PWDN_GPIO_NUM 32
#define RESET_GPIO_NUM -1
#define XCLK_GPIO_NUM 0
#define SIOD_GPIO_NUM 26
#define SIOC_GPIO_NUM 27
#define Y9_GPIO_NUM 35
#define Y8_GPIO_NUM 34
#define Y7_GPIO_NUM 39
#define Y6_GPIO_NUM 36
#define Y5_GPIO_NUM 21
#define Y4_GPIO_NUM 19
#define Y3_GPIO_NUM 18
#define Y2_GPIO_NUM 5
#define VSYNC_GPIO_NUM 25
#define HREF_GPIO_NUM 23
#define PCLK_GPIO_NUM 22
#define FLASH_LED_PIN 4

const char* ssid = "Carthasis";
const char* pass = "12345678";
const char* api_server = "http://localhost:5000/upload";
bool LED_Flash_ON = true;
int count = 1;

String cameraToBase64(camera_fb_t* fb);
String takePhoto();
void sendPhoto(String imageString);

void setup() {
  WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0);
  Serial.begin(115200);

  // Internet: use WiFi
  Serial.println("ESP32CAM: Setup Internet connection");
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("INFO: Conntecting to WiFi...");
    delay(1000);
  }
  Serial.println("INFO: Connected to WiFi.");

  // Camera: configuration
  Serial.println("ESP32CAM: Setup Camera ESP32CAM");
  pinMode(FLASH_LED_PIN, OUTPUT);

  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sccb_sda = SIOD_GPIO_NUM;
  config.pin_sccb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;


  // Camera: high specs to pre-allocate larger buffers
  if (psramFound()) {
    config.frame_size = FRAMESIZE_SVGA;
    config.jpeg_quality = 10;  //0-63 lower number means higher quality
    config.fb_count = 2;
  } else {
    config.frame_size = FRAMESIZE_CIF;
    config.jpeg_quality = 8;  //0-63 lower number means higher quality
    config.fb_count = 1;
  }

  // Camera: Initialize
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("ERROR: Camera init failed with error 0x%x", err);
    Serial.println();
    Serial.printf("ESP32: Machine restarting");
    delay(1000);
    ESP.restart();
  }
}

void loop() {
  delay(2000);
  if (count == 1) {
    String imageString = takePhoto();
    if (imageString == NULL) {
      Serial.println("Camera does not take any photo");
      Serial.println("Restarting the ESP32 CAM.");
      delay(1000);
      ESP.restart();
      return;
    }
    if (WiFi.status() == WL_CONNECTED) {
      sendPhoto(imageString);
    }
    count = 2;
  }
  delay(50000);
}

// Take Photo
String takePhoto() {
  if (LED_Flash_ON == true) {
    digitalWrite(FLASH_LED_PIN, HIGH);
    delay(1000);
  }

  for (int i = 0; i <= 3; i++) {
    camera_fb_t* fb = esp_camera_fb_get();
    if (!fb) {
      Serial.println("Camera capture failed");
      Serial.println("Restarting the ESP32 CAM.");
      delay(1000);
      ESP.restart();
      return "";
    }
    esp_camera_fb_return(fb);
    delay(200);
  }

  camera_fb_t* fb = esp_camera_fb_get();
  if (!fb) {
    Serial.println("Camera capture failed");
    Serial.println("Restarting the ESP32 CAM.");
    delay(1000);
    ESP.restart();
    return "";
  }

  if (LED_Flash_ON == true) {
    digitalWrite(FLASH_LED_PIN, LOW);
  }

  Serial.println("Taking a photo was successful.");

  return cameraToBase64(fb);
}

void sendPhoto(String imageString) {
  HTTPClient http;
  if (WiFi.status() == WL_CONNECTED) {
    http.begin("https://67a7-103-191-218-211.ngrok-free.app/upload");
    http.addHeader("Content-Type", "application/json");
    int httpCode = http.POST("{\"image\": \"" + imageString + "\"}");

    Serial.println("statusCode: " + httpCode);
    if (httpCode == HTTP_CODE_OK) {
      String payload = http.getString();
      Serial.println("response: " + payload);
    } else {
      Serial.printf("HTTP failed, error: %s\n", http.errorToString(httpCode).c_str());
    }
  } else {
    Serial.println("Unconnected to WiFi");
  }
  http.end();
}

// Convert file to Base64
String cameraToBase64(camera_fb_t* fb) {
  uint8_t* fbBuf = fb->buf;
  size_t fbLen = fb->len;

  return base64::encode(fbBuf, fbLen);
}
