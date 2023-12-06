#define FLASH_LED_PIN 4

void setup() {
  pinMode(FLASH_LED_PIN, OUTPUT);
}

void loop() {
  digitalWrite(FLASH_LED_PIN, HIGH); // turn on flash LED
  delay(1000); // wait for a second
  digitalWrite(FLASH_LED_PIN, LOW); // turn off flash LED
  delay(1000); // wait for a second
}
