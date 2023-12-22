#include <Arduino.h>
#include <ArduinoJson.h>
#include <UniversalTelegramBot.h>
#include <klasifikasi-jenis-sampah_inferencing.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include "edge-impulse-sdk/dsp/image/image.hpp"
#include "esp_camera.h"
#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"

// camera model_ai_thinker
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
#define EI_CAMERA_RAW_FRAME_BUFFER_COLS 320
#define EI_CAMERA_RAW_FRAME_BUFFER_ROWS 240
#define EI_CAMERA_FRAME_BYTE_SIZE 3
// internet, telegram
#define WIFI_SSID "Carthasis"
#define WIFI_PASSWD "12345678"
#define TELE_BOT_TOKEN ""
#define TELE_CHAT_ID ""

// machine learning, camera variables
static bool debug_nn = false;
static bool is_initialized = false;
uint8_t *snapshot_buf;  //points to the output of the capture
bool is_classify = true;
// internet, telegram variables
bool is_life = true;
WiFiClientSecure secured_client;
UniversalTelegramBot bot(TELE_BOT_TOKEN, secured_client);

// function definitions
bool ks_camera_init(void);
void ks_camera_deinit(void);
bool ks_camera_capture(uint32_t img_width, uint32_t img_height, uint8_t *out_buf);
String ks_classify_image(uint8_t *snapshot_buf);
String tele_send_photo();

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
  while (!Serial);

  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  secured_client.setCACert(TELEGRAM_CERTIFICATE_ROOT);  // Add root certificate for api.telegram.org
  Serial.print("\ninfo: internet connecting\n");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
  }

  while(ks_camera_init() == false) {
    ei_printf("error: camera fail initialezed\n");
  }

  Serial.print("info: program ready\n");
  delay(1000);
}

void loop() {
  if (is_life) {
    bot.sendMessage(TELE_CHAT_ID, "Bang Agus, your ESP32Cam is life!", "");
    Serial.printf("info: life notify sent\n");
    is_life = false;
    delay(100);
  }
  if (is_classify == true) {
    tele_send_photo();

    Serial.printf("info: image has been processed\n");
    is_classify = false;
  }
  // if (is_classify == true) {
  //   // instead of wait_ms, we'll wait on the signal, this allows threads to cancel us...
  //   if (ei_sleep(5) != EI_IMPULSE_OK) {
  //     return;
  //   }

  //   snapshot_buf = (uint8_t *)malloc(EI_CAMERA_RAW_FRAME_BUFFER_COLS * EI_CAMERA_RAW_FRAME_BUFFER_ROWS * EI_CAMERA_FRAME_BYTE_SIZE);
  //   // check if allocation was successful
  //   if (snapshot_buf == nullptr) {
  //     ei_printf("\nerror: failed to allocate snapshot buffer!\n");
  //     return;
  //   }
  //   // Classify
  //   String result = ks_classify_image(snapshot_buf);
  //   bot.sendMessage(TELE_CHAT_ID, "The image is " + result, "");
  //   Serial.println("The object is " + result);

  //   free(snapshot_buf);
  //   is_classify = false;
  // }
  delay(1000);
}

// image sensor up
bool ks_camera_init(void) {
  if (is_initialized) return true;

  esp_err_t err = esp_camera_init(&camera_config);
  if (err != ESP_OK) {
    Serial.printf("error: camera init failed with error 0x%x\n", err);
    return false;
  }

  sensor_t *s = esp_camera_sensor_get();
  if (s->id.PID == OV3660_PID) {
    s->set_vflip(s, 1);       // flip it back
    s->set_brightness(s, 1);  // up the brightness just a bit
    s->set_saturation(s, 0);  // lower the saturation
  }

  Serial.printf("info: camera ready\n");
  is_initialized = true;
  return true;
}

// image sensor down
void ks_camera_deinit(void) {
  esp_err_t err = esp_camera_deinit();
  if (err != ESP_OK) {
    ei_printf("error: camera deinit failed\n");
    return;
  }

  Serial.printf("info: camera deinit\n");
  is_initialized = false;
  return;
}

// capture, rescale and crop image (width. height, pointer)
bool ks_camera_capture(uint32_t img_width, uint32_t img_height, uint8_t *out_buf) {
  bool do_resize = false;
  if (!is_initialized) {
    ei_printf("error: camera is not initialized\n");
    return false;
  }

  camera_fb_t *fb = esp_camera_fb_get();
  if (!fb) {
    ei_printf("\error: camera capture failed\n");
    return false;
  }

  bool converted = fmt2rgb888(fb->buf, fb->len, PIXFORMAT_JPEG, snapshot_buf);
  esp_camera_fb_return(fb);
  if (!converted) {
    ei_printf("error: conversion failed\n");
    return false;
  }

  if ((img_width != EI_CAMERA_RAW_FRAME_BUFFER_COLS)
      || (img_height != EI_CAMERA_RAW_FRAME_BUFFER_ROWS)) {
    do_resize = true;
  }

  if (do_resize) {
    ei::image::processing::crop_and_interpolate_rgb888(
      out_buf,
      EI_CAMERA_RAW_FRAME_BUFFER_COLS,
      EI_CAMERA_RAW_FRAME_BUFFER_ROWS,
      out_buf,
      img_width,
      img_height);
  }

  return true;
}

// classify into organik or anorgranik
String ks_classify_image(uint8_t *snapshot_buf) {
  ei::signal_t signal;
  signal.total_length = EI_CLASSIFIER_INPUT_WIDTH * EI_CLASSIFIER_INPUT_HEIGHT;
  signal.get_data = &ei_camera_get_data;

  if (ks_camera_capture((size_t)EI_CLASSIFIER_INPUT_WIDTH, (size_t)EI_CLASSIFIER_INPUT_HEIGHT, snapshot_buf) == false) {
    // ei_printf("Failed to capture image\r\n");
    free(snapshot_buf);
    return "failed to capture image";
  }

  // run the classifier
  ei_impulse_result_t result = { 0 };

  EI_IMPULSE_ERROR err = run_classifier(&signal, &result, debug_nn);
  if (err != EI_IMPULSE_OK) {
    return "failed to run classifier";
  }

  // result
  int index;
  float score = 0.0;
#if EI_CLASSIFIER_OBJECT_DETECTION == 1
  for (size_t ix = 0; ix < result.bounding_boxes_count; ix++) {
    auto bb = result.bounding_boxes[ix];
    if (bb.value == 0) {
      continue;
    }
    // ei_printf("\n    %s (%f) [ x: %u, y: %u, width: %u, height: %u ]\n", bb.label, bb.value, bb.x, bb.y, bb.width, bb.height);

    // record the most possible label
    if (bb.value > score) {
      score = bb.value;
      index = ix;
    }
  }
  if (result.bounding_boxes[0].value <= 0) {
    ei_printf("    No objects found\n");
  }
#else
  for (size_t ix = 0; ix < EI_CLASSIFIER_LABEL_COUNT; ix++) {
    // record the most possible label
    if (result.classification[ix].value > score) {
      score = result.classification[ix].value;
      index = ix;
    }
    // ei_printf("\n    %s: %.5f\n", result.classification[ix].label, result.classification[ix].value);
  }
#endif

#if EI_CLASSIFIER_HAS_ANOMALY == 1
  ei_printf("    anomaly score: %.3f\n", result.anomaly);
#endif

  return String(result.classification[index].label);  // return the most possible label
}

String tele_send_photo() {
  String tele_bot_token = "";
  String tele_chat_id = "";
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
    Serial.printf("\ninfo: camera capture failed");
    delay(1000);
    ESP.restart();
    return "camera capture failed";
  }

  Serial.printf("info: connect to %s\n",String(myDomain));

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
    Serial.print("error: connected to api.telegram.org failed.\n");
  }
  return getBody;
}

// retrieve RGB image data from buffer and store in float array
static int ei_camera_get_data(size_t offset, size_t length, float *out_ptr) {
  // we already have a RGB888 buffer, so recalculate offset into pixel index
  size_t pixel_ix = offset * 3;
  size_t pixels_left = length;
  size_t out_ptr_ix = 0;

  while (pixels_left != 0) {
    // Extract RGB values from the snapshot_buf and store them as a single float
    out_ptr[out_ptr_ix] = (snapshot_buf[pixel_ix] << 16) + (snapshot_buf[pixel_ix + 1] << 8) + snapshot_buf[pixel_ix + 2];
    // go to the next pixel
    out_ptr_ix++;
    pixel_ix += 3;
    pixels_left--;
  }
  // and done!
  return 0;
}

