import network
from machine import Pin
from time import sleep


ssid = 'Home'
password = '12345678'

nic = network.WLAN(network.STA_IF)
nic.active(True)

try:
    nic.connect(ssid, password)
except OSError as e:
    print('Error:', e)

led = Pin(2, Pin.OUT)

while not nic.isconnected():
    sleep(1)  # wait for connection

led.value(1)  # turn on LED when connected

