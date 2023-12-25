#include <klasifikasi-jenis-sampah_inferencing.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <HTTPClient.h>
#include <addons/TokenHelper.h>
#include "esp_camera.h"
#include "edge-impulse-sdk/dsp/image/image.hpp"
#include "soc/soc.h"
#include "soc/rtc_cntl_reg.h"

// Camera defines
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

// Constant defines
#define EI_CAMERA_RAW_FRAME_BUFFER_COLS 320
#define EI_CAMERA_RAW_FRAME_BUFFER_ROWS 240
#define EI_CAMERA_FRAME_BYTE_SIZE 3
#define WIFI_SSID "Carthasis"
#define WIFI_PASSWD "12345678"
#define FB_API_KEY ""
#define FB_PROJECT_ID ""
#define FB_EMAIL "esp32cam@test.com"
#define FB_PASSWD "passwd123"

// Private variables
static bool debug_nn = false;
static bool is_initialised = false;
uint8_t *snapshot_buf;  //points to the output of the capture
FirebaseData fbdo;
FirebaseAuth fbauth;
FirebaseConfig fbconfig;
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;

bool classification = true;
int count = 0;

// Function definitions
bool ei_camera_init(void);
void ei_camera_deinit(void);
bool ei_camera_capture(uint32_t img_width, uint32_t img_height, uint8_t *out_buf);
void logger_initializer(void);
void logger_writter(String result);

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
  logger_initializer();

  Serial.println("Setting Up Program...");
  if (ei_camera_init() == false) {
    ei_printf("ERR: Failed to initialize Camera!\r\n");
  } else {
    ei_printf("INFO: Camera initialized\r\n");
  }

  ei_printf("\nStarting program in 3 seconds...\n");
  ei_sleep(2000);
}

void loop() {
  delay(1000);
  if (classification == true) {
    // instead of wait_ms, we'll wait on the signal, this allows threads to cancel us...
    if (ei_sleep(5) != EI_IMPULSE_OK) {
      return;
    }

    snapshot_buf = (uint8_t *)malloc(EI_CAMERA_RAW_FRAME_BUFFER_COLS * EI_CAMERA_RAW_FRAME_BUFFER_ROWS * EI_CAMERA_FRAME_BYTE_SIZE);
    // check if allocation was successful
    if (snapshot_buf == nullptr) {
      ei_printf("ERR: Failed to allocate snapshot buffer!\n");
      return;
    }

    ei::signal_t signal;
    signal.total_length = EI_CLASSIFIER_INPUT_WIDTH * EI_CLASSIFIER_INPUT_HEIGHT;
    signal.get_data = &ei_camera_get_data;

    if (ei_camera_capture((size_t)EI_CLASSIFIER_INPUT_WIDTH, (size_t)EI_CLASSIFIER_INPUT_HEIGHT, snapshot_buf) == false) {
      ei_printf("Failed to capture image\r\n");
      free(snapshot_buf);
      return;
    }

    // Run the classifier
    ei_impulse_result_t result = { 0 };

    EI_IMPULSE_ERROR err = run_classifier(&signal, &result, debug_nn);
    if (err != EI_IMPULSE_OK) {
      ei_printf("ERR: Failed to run classifier (%d)\n", err);
      return;
    }

    int index;
  float score = 0.0;
#if EI_CLASSIFIER_OBJECT_DETECTION == 1
  for (size_t ix = 0; ix < result.bounding_boxes_count; ix++) {
    auto bb = result.bounding_boxes[ix];
    if (bb.value == 0) {
      continue;
    }
    ei_printf("    %s (%f) [ x: %u, y: %u, width: %u, height: %u ]\n", bb.label, bb.value, bb.x, bb.y, bb.width, bb.height);

    // Record the most possible label
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
    // Record the most possible label
    if (result.classification[ix].value > score) {
      score = result.classification[ix].value;
      index = ix;
    }
    ei_printf("    %s: %.5f\n", result.classification[ix].label, result.classification[ix].value);
  }
#endif

#if EI_CLASSIFIER_HAS_ANOMALY == 1
  ei_printf("    anomaly score: %.3f\n", result.anomaly);
#endif

    logger_writter(result.classification[index].label);
    Serial.println("Object is " + String(result.classification[index].label));

    free(snapshot_buf);

    
    classification = false;
    
  }
}

// Setup image sensor & start streaming
bool ei_camera_init(void) {
  if (is_initialised) return true;

  // initialize the camera
  esp_err_t err = esp_camera_init(&camera_config);
  if (err != ESP_OK) {
    Serial.printf("ERR: Camera init failed with error 0x%x\n", err);
    return false;
  }

  sensor_t *s = esp_camera_sensor_get();
  // initial sensors are flipped vertically and colors are a bit saturated
  if (s->id.PID == OV3660_PID) {
    s->set_vflip(s, 1);       // flip it back
    s->set_brightness(s, 1);  // up the brightness just a bit
    s->set_saturation(s, 0);  // lower the saturation
  }

#if defined(CAMERA_MODEL_M5STACK_WIDE)
  s->set_vflip(s, 1);
  s->set_hmirror(s, 1);
#endif

  is_initialised = true;
  return true;
}

// Stop streaming of sensor data
void ei_camera_deinit(void) {
  //deinitialize the camera
  esp_err_t err = esp_camera_deinit();
  if (err != ESP_OK) {
    ei_printf("ERR: Camera deinit failed\n");
    return;
  }

  is_initialised = false;
  return;
}

// Capture, rescale and crop image (width. height, pointer)
bool ei_camera_capture(uint32_t img_width, uint32_t img_height, uint8_t *out_buf) {
  bool do_resize = false;
  if (!is_initialised) {
    ei_printf("ERR: Camera is not initialized\r\n");
    return false;
  }

  camera_fb_t *fb = esp_camera_fb_get();
  if (!fb) {
    ei_printf("ERR: Camera capture failed\n");
    return false;
  }

  bool converted = fmt2rgb888(fb->buf, fb->len, PIXFORMAT_JPEG, snapshot_buf);
  esp_camera_fb_return(fb);
  if (!converted) {
    ei_printf("ERR: Conversion failed\n");
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

// Retrieve RGB image data from buffer and store in float array
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

// Setup logger & start write into firebase
void logger_initializer(void) {
  WiFi.begin(WIFI_SSID, WIFI_PASSWD);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("ERR: Internet connection fail");
  }
  Serial.println("INFO: Internet connected");

  fbconfig.api_key = FB_API_KEY;
  if (!Firebase.signUp(&fbconfig, &fbauth, "", "")) {
    Serial.println("ERR: SignUp firebase failed");
  }

  signupOK = true;
  Serial.println("INFO: SignUp firebase successfully");
  fbconfig.token_status_callback = tokenStatusCallback;
  Firebase.reconnectWiFi(true);
  Firebase.begin(&fbconfig, &fbauth);
}

// logger
void logger_writter(String result) {
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
    //json.set("fields/image/stringValue", image);
    json.set("fields/result/stringValue", result);
    json.set("fields/classified_at/stringValue", timeString);

    if (!Firebase.Firestore.createDocument(&fbdo, FB_PROJECT_ID, "", documentPath.c_str(), json.raw())) {
      Serial.println("ERR: Write log failed");
    }

    Serial.println("INFO: Write log successfully");
  }
}
