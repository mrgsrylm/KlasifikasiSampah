#include <ESP32Servo.h>

#define TRIG_PIN  13  // ultrasonic sensor trigger/output pin
#define ECHO_PIN  14  // ultrasonic sensor echo/input pin
#define SERVO_PIN 16  // servo motor pin
#define DISTANCE_THRESHOLD  50 // centimeters

Servo servo; // create servo object to control a servo

// variables will change:
float duration_us, distance_cm;

String result = "anorganik"; 

void setup() {
  Serial.begin (9600);       
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
  long duration = pulseIn(ECHO_PIN, HIGH);
  int jarak = duration / 58.2;

  // cek jika jarak kurang dari 15 cm
  if (jarak < 25) {
    return true;
  } else {
    return false;
  }
}

void motor_direction(String result) {
  if(result == "organik") {
    servo.write(180);
  }
  if(result == "anorganik"){
    servo.write(0);
  }
  delay(2000);
  servo.write(90);
}

void loop() {
  bool isDetect = detectObject();
  if(isDetect) {
    motor_direction(result);
  }
}
