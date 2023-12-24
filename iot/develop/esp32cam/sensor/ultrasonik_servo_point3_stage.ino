#include <ESP32Servo.h>

#define TRIG_PIN  13  // ultrasonic sensor trigger/output pin
#define ECHO_PIN  14  // ultrasonic sensor echo/input pin
#define SERVO_PIN 16  // servo motor pin
#define DISTANCE_THRESHOLD  25 // centimeters

Servo servo; // create servo object to control a servo

// variables will change:
float duration_us, distance_cm;
unsigned long previousMillis = 0;
const long interval = 1000;  // interval in milliseconds

char result[10] = "anorganik";

void setup() {
  Serial.begin(9600);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  servo.attach(SERVO_PIN);
  servo.write(90); // default
}

bool detectObject() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  // hitung jarak berdasarkan kecepatan suara, jarak (cm) = Durasi (us) / 58.2
  duration_us = pulseIn(ECHO_PIN, HIGH);
  distance_cm = duration_us / 58.2;

  // cek jika jarak kurang dari threshold
  return (distance_cm < DISTANCE_THRESHOLD);
}

void motor_direction(const char result[]) {
  if (strcmp(result, "organik") == 0) {
    servo.write(180);
  } else if (strcmp(result, "anorganik") == 0) {
    servo.write(0);
  }
  delay(3000);
  servo.write(90);
}

void loop() {
  unsigned long currentMillis = millis();

  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;  // save the last time we blinked the LED

    bool isDetect = detectObject();
    if (isDetect) {
      motor_direction(result);
    }
  }
}
