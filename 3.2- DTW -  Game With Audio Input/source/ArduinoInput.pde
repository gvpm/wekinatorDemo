import processing.serial.*;
import cc.arduino.*;
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress dest;
Arduino arduino;
int sensor = 03;
int potentiometer = 04;
int sensorRead,potentiometerRead;
PFont myBigFont;

void setup()
{
  println(Arduino.list());
  arduino = new Arduino(this, Arduino.list()[0], 57600);
  arduino.pinMode(sensor, Arduino.INPUT);
  arduino.pinMode(potentiometer, Arduino.INPUT);
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,9000);
  dest = new NetAddress("127.0.0.1",6448);
  size(400,400);
  myBigFont = createFont("Arial", 50);
}

void draw()
{
  background(300);
  sensorRead = arduino.analogRead(sensor);

  potentiometerRead = arduino.analogRead(potentiometer);
  
  
  OscMessage msg = new OscMessage("/wek/inputs");
  msg.add((float)sensorRead); 
  msg.add((float)potentiometerRead);
  oscP5.send(msg, dest);
  stroke(0);
  textFont(myBigFont);
  text("Sensor  "+sensorRead, 10, 180);
  text("Pot  "+potentiometerRead, 10, 250);
  
  

}