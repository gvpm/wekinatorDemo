import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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

public class Output extends PApplet {

//This demo triggers a text display with each new message
// Works with 1 classifier output, any number of classes
//Listens on port 12000 for message /wek/outputs (defaults)

//Necessary for OSC communication with Wekinator:


OscP5 oscP5;
NetAddress dest;


//No need to edit:
PFont myFont, myBigFont;
final int myHeight = 400;
final int myWidth = 400;
int frameNum = 0;
int currentHue = 100;
int currentTextHue = 255;
String currentMessage = "";

public void setup() {
  //Initialize OSC communication
  oscP5 = new OscP5(this,12000); //listen for OSC messages on port 12000 (Wekinator default)
  dest = new NetAddress("127.0.0.1",6448); //send messages back to Wekinator on port 6448, localhost (this machine) (default)
  
  colorMode(HSB);
  
  
  background(255);
  
  String typeTag = "f";
  //myFont = loadFont("SansSerif-14.vlw");
  myFont = createFont("Arial", 14);
  myBigFont = createFont("Arial", 80);
}

public void draw() {
  frameRate(30);
  background(currentHue, 255, 255);
  drawText();
}

//This is called automatically when OSC message is received
public void oscEvent(OscMessage theOscMessage) {
 println("received message");
    if (theOscMessage.checkAddrPattern("/wek/outputs") == true) {
      if(theOscMessage.checkTypetag("f")) {
      float f = theOscMessage.get(0).floatValue();
      println("received1");
       showMessage((int)f);
      }
    }
 
}

public void showMessage(int i) {
    currentHue = (int)generateColor(i);
    currentTextHue = (int)generateColor((i+1));
    currentMessage = Integer.toString(i);
}

//Write instructions to screen.
public void drawText() {
    stroke(0);
    textFont(myFont);
    textAlign(LEFT, TOP); 
    fill(currentTextHue, 255, 255);

    text("Receives 1 classifier output message from wekinator", 10, 10);
    text("Listening for OSC message /wek/outputs, port 12000", 10, 30);
    
    textFont(myBigFont);
    text(currentMessage, 190, 180);
}


public float generateColor(int which) {
  float f = 100; 
  int i = which;
  if (i <= 0) {
     return 100;
  } 
  else {
     return (generateColor(which-1) + 1.61f*255) %255; 
  }
}
  public void settings() {  size(400,400, P3D);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Output" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
