#include <ESP32Servo.h>

#define TRIG_PIN  13  // ESP32 pin GPIO23 connected to Ultrasonic Sensor's TRIG pin
#define ECHO_PIN  14  // ESP32 pin GPIO22 connected to Ultrasonic Sensor's ECHO pin
#define SERVO_PIN 16  // ESP32 pin GPIO26 connected to Servo Motor's pin
#define DISTANCE_THRESHOLD  50 // centimeters

Servo servo; // create servo object to control a servo

// variables will change:
float duration_us, distance_cm;

void setup() {
  Serial.begin (9600);       // initialize serial port
  pinMode(TRIG_PIN, OUTPUT); // set ESP32 pin to output mode
  pinMode(ECHO_PIN, INPUT);  // set ESP32 pin to input mode
  servo.attach(SERVO_PIN);   // attaches the servo on pin 9 to the servo object
  servo.write(0);
}


bool detectObject() {
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  long duration = pulseIn(ECHO_PIN, HIGH);

  // Hitung jarak berdasarkan kecepatan suara
  // Jarak (cm) = Durasi (us) / 58.2
  int jarak = duration / 58.2;

  // Cek jika jarak kurang dari 15 cm
  if (jarak < 15) {
    return true;
  } else {
    return false;
  }
}

void loop() {
  bool isDetect = detectObject();
  if(isDetect == true) {
    servo.write(90);
  }else{
    servo.write(0);
  }
  delay(1000);
}

