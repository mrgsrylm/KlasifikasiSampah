#include <WiFi.h>
#include <UniversalTelegramBot.h>
#include <WiFiClientSecure.h>
#include <Arduino.h>
#include <ArduinoJson.h>
#include "esp_camera.h"
#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"

//CAMERA_MODEL_AI_THINKER
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

#define WIFI_SSID "Carthasis"
#define WIFI_PASSWD "12345678"
#define TELE_BOT_TOKEN ""
#define TELE_CHAT_ID ""


//Checks for new messages every 1 second.
int botRequestDelay = 1000;
unsigned long lastTimeBotRan;
bool flashState = LOW;
bool sendPhoto = true;
bool life = true;

WiFiClientSecure secured_client;
UniversalTelegramBot bot(TELE_BOT_TOKEN, secured_client);

String tele_send_photo();
void tele_handle_message(int message);

static camera_config_t camera_config = {
  .pin_pwdn = PWDN_GPIO_NUM,
  .pin_reset = RESET_GPIO_NUM,
  .pin_xclk = XCLK_GPIO_NUM,
  .pin_sscb_sda = SIOD_GPIO_NUM,
  .pin_sscb_scl = SIOC_GPIO_NUM,

  .pin_d7 = Y9_GPIO_NUM,
  .pin_d6 = Y8_GPIO_NUM,
  .pin_d5 = Y7_GPIO_NUM,
  .pin_d4 = Y6_GPIO_NUM,
  .pin_d3 = Y5_GPIO_NUM,
  .pin_d2 = Y4_GPIO_NUM,
  .pin_d1 = Y3_GPIO_NUM,
  .pin_d0 = Y2_GPIO_NUM,
  .pin_vsync = VSYNC_GPIO_NUM,
  .pin_href = HREF_GPIO_NUM,
  .pin_pclk = PCLK_GPIO_NUM,

  //XCLK 20MHz or 10MHz for OV2640 double FPS (Experimental)
  .xclk_freq_hz = 20000000,
  .ledc_timer = LEDC_TIMER_0,
  .ledc_channel = LEDC_CHANNEL_0,

  .pixel_format = PIXFORMAT_JPEG,  //YUV422,GRAYSCALE,RGB565,JPEG
  .frame_size = FRAMESIZE_QVGA,    //QQVGA-UXGA Do not use sizes above QVGA when not JPEG

  .jpeg_quality = 12,  //0-63 lower number means higher quality
  .fb_count = 1,       //if more than one, i2s runs in continuous mode. Use only with JPEG
  .fb_location = CAMERA_FB_IN_PSRAM,
  .grab_mode = CAMERA_GRAB_WHEN_EMPTY,
};

void setup() {
  WRITE_PERI_REG(RTC_CNTL_BROWN_OUT_REG, 0);
  Serial.begin(115200);
  while (!Serial)
    ;

  pinMode(FLASH_LED_PIN, OUTPUT);
  digitalWrite(FLASH_LED_PIN, flashState);

  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  secured_client.setCACert(TELEGRAM_CERTIFICATE_ROOT);  // Add root certificate for api.telegram.org
  Serial.print("info: internet connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
  }
  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  Serial.print("\ninfo: internet connected. ip: ");
  Serial.println(WiFi.localIP());

  esp_err_t err = esp_camera_init(&camera_config);
  if (err != ESP_OK) {
    Serial.printf("error: camera init failed with error 0x%x\n", err);
    delay(1000);
  }
  Serial.println("info: program already");
}

void loop() {
  if (life) {
    bot.sendMessage(TELE_CHAT_ID, "Bang Agus, ESP32Cam is life!", "");
    life = false;
  }
  if (sendPhoto) {
    Serial.println("Preparing photo");
    tele_send_photo();
    sendPhoto = false;
  }
  if (millis() > lastTimeBotRan + botRequestDelay) {
    int numNewMessages = bot.getUpdates(bot.last_message_received + 1);
    while (numNewMessages) {
      Serial.println("info: getting responses");
      tele_handle_message(numNewMessages);
      numNewMessages = bot.getUpdates(bot.last_message_received + 1);
    }
    lastTimeBotRan = millis();
  }
}

void tele_handle_message(int message) {
  for (int i = 0; i < message; i++) {
    String chat_id = String(bot.messages[i].chat_id);
    if (chat_id != TELE_CHAT_ID) {
      bot.sendMessage(chat_id, "error: unauthorized");
      continue;
    }
    String text = bot.messages[i].text;
    String from_name = bot.messages[i].from_name;
    if (text == "/start") {
      String welcome = "Hi, " + from_name + "\n";
      welcome += "Use the following commands to interact with the ESP32-CAM \n";
      welcome += "/photo : takes a new photo\n";
      welcome += "/flash : toggles flash LED \n";
      bot.sendMessage(TELE_CHAT_ID, welcome, "");
    }
    if (text == "/flash") {
      flashState = !flashState;
      digitalWrite(FLASH_LED_PIN, flashState);
      Serial.println("Change flash LED state");
    }
    if (text == "/photo") {
      sendPhoto = true;
      Serial.println("New photo request");
    }
  }
}

String tele_send_photo() {
  String tele_chat_id = "";
  String tele_bot_token = "";

  const char* myDomain = "api.telegram.org";
  String getAll = "";
  String getBody = "";

  //Dispose first picture because of bad quality
  camera_fb_t* fb = NULL;
  fb = esp_camera_fb_get();
  esp_camera_fb_return(fb);  // dispose the buffered image

  // Take a new photo
  fb = NULL;
  fb = esp_camera_fb_get();
  if (!fb) {
    Serial.println("info: camera capture failed");
    delay(1000);
    ESP.restart();
    return "camera capture failed";
  }

  Serial.println("info: connect to " + String(myDomain));

  if (secured_client.connect(myDomain, 443)) {
    Serial.println("info: connection successfully");
    String head = "--PhotoBotESP32Cam\r\nContent-Disposition: form-data; name=\"chat_id\"; \r\n\r\n" + tele_chat_id + "\r\n--PhotoBotESP32Cam\r\nContent-Disposition: form-data; name=\"photo\"; filename=\"esp32-cam.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n";
    String tail = "\r\n--PhotoBotESP32Cam--\r\n";

    size_t imageLen = fb->len;
    size_t extraLen = head.length() + tail.length();
    size_t totalLen = imageLen + extraLen;

    secured_client.println("POST /bot" + tele_bot_token + "/sendPhoto HTTP/1.1");
    secured_client.println("Host: " + String(myDomain));
    secured_client.println("Content-Length: " + String(totalLen));
    secured_client.println("Content-Type: multipart/form-data; boundary=PhotoBotESP32Cam");
    secured_client.println();
    secured_client.print(head);

    uint8_t* fbBuf = fb->buf;
    size_t fbLen = fb->len;
    for (size_t n = 0; n < fbLen; n = n + 1024) {
      if (n + 1024 < fbLen) {
        secured_client.write(fbBuf, 1024);
        fbBuf += 1024;
      } else if (fbLen % 1024 > 0) {
        size_t remainder = fbLen % 1024;
        secured_client.write(fbBuf, remainder);
      }
    }

    secured_client.print(tail);

    esp_camera_fb_return(fb);

    int waitTime = 10000;  // timeout 10 seconds
    long startTimer = millis();
    boolean state = false;

    while ((startTimer + waitTime) > millis()) {
      Serial.print(".");
      delay(100);
      while (secured_client.available()) {
        char c = secured_client.read();
        if (state == true) getBody += String(c);
        if (c == '\n') {
          if (getAll.length() == 0) state = true;
          getAll = "";
        } else if (c != '\r')
          getAll += String(c);
        startTimer = millis();
      }
      if (getBody.length() > 0) break;
    }
    secured_client.stop();
    Serial.println(getBody);
  } else {
    getBody = "connected to api.telegram.org failed.";
    Serial.println("error: connected to api.telegram.org failed.");
  }
  return getBody;
}
