import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import cc.arduino.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ArduinoInput extends PApplet {






OscP5 oscP5;
NetAddress dest;
Arduino arduino;
int sensor = 03;
int potentiometer = 04;
int sensorRead,potentiometerRead;
PFont myBigFont;

public void setup()
{
  println(Arduino.list());
  arduino = new Arduino(this, Arduino.list()[1], 57600);
  arduino.pinMode(sensor, Arduino.INPUT);
  arduino.pinMode(potentiometer, Arduino.INPUT);
  /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,9000);
  dest = new NetAddress("127.0.0.1",6448);
  
  myBigFont = createFont("Arial", 50);
}

public void draw()
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
  public void settings() {  size(400,400); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ArduinoInput" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
