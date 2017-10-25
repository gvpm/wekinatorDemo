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

/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/17115*@* */
/* !do not delete the line above, required for linking your tweak if you upload again */
/* Modified by Rebecca Fiebrink to work with Wekinator */
/* Receives DTW commands /output_1, /output_2, /output_3 (the default messages for 1st 3 gestures) on port 12000 */




OscP5 oscP5;
NetAddress dest;

final int WIDTH = 30;
final int HEIGHT = 23;
int[][] level = new int[HEIGHT][WIDTH];

int rightCount = 0;
int leftCount = 0;


Player p1;

//booleans for key presses to get a simple yes or no press and 
//to not have to worry about the a,aaaaaaaaaaaaa thing
boolean right = false, left = false, up = false;

public void setup() {
  oscP5 = new OscP5(this, 12000); //listen for OSC messages on port 12000 (Wekinator default)
  dest = new NetAddress("127.0.0.1", 6448); //send messages back to Wekinator on port 6448, localhost (this machine) (default)
  
  p1 = new Player(WIDTH*8, HEIGHT*8); //put the player in the middle of the window
}
public void draw() {
  p1.update();

  background(200);
  drawLevel();
  p1.show();

  if (rightCount > 0) {
    rightCount--;
    if (rightCount == 0) {
      right = false;
    }
  }

  if (leftCount > 0) {
    leftCount--;
    if (leftCount == 0) {
      left = false;
    }
  }
  up = false;

  drawText();
}

public void drawLevel() {
  fill(0);
  noStroke();
  for ( int ix = 0; ix < WIDTH; ix++ ) {
    for ( int iy = 0; iy < HEIGHT; iy++ ) {
      switch(level[iy][ix]) {
      case 1: 
        rect(ix*16, iy*16, 16, 16);
      }
    }
  }
}

public boolean place_free(int xx, int yy) {
  //checks if a given point (xx,yy) is free (no block at that point) or not
  yy = PApplet.parseInt(floor(yy/16.0f));
  xx = PApplet.parseInt(floor(xx/16.0f));
  if ( xx > -1 && xx < level[0].length && yy > -1 && yy < level.length ) {
    if ( level[yy][xx] == 0 ) {
      return true;
    }
  }
  return false;
}

public void keyPressed() {
  switch(keyCode) {
  case RIGHT: 
    right = true; 
    break;
  case LEFT: 
    left = true; 
    break;
  case UP: 
    up = true; 
    break;
  }
}
public void keyReleased() {
  switch(keyCode) {
  case RIGHT: 
    right = false; 
    break;
  case LEFT: 
    left = false; 
    break;
  case UP: 
    up = false; 
    break;
  }
}
public void mousePressed() {
  //Left click creates/destroys a block
  if ( mouseButton == LEFT ) {
    level[PApplet.parseInt(floor(mouseY/16.0f))][PApplet.parseInt(floor(mouseX/16.0f))] ^= 1;
  }
}

//This is called automatically when OSC message is received
public void oscEvent(OscMessage theOscMessage) {

  if (theOscMessage.checkAddrPattern("/wek/outputs") == true) {
    if (theOscMessage.checkTypetag("f")) {
      float f = theOscMessage.get(0).floatValue();
      println("received1");
      if ((int)f == 1) {
        goLeft();
        println("left");
      } else if ((int)f == 2) {
        goRight();
        println("right");
      } else if ((int)f == 3) {
        jump();
        println("jump");
      } else {
        println("Unknown OSC message received");
      }
    }
  }
}

public void drawText() {
  
  text("Receives 1 classifier output message from Wekinator", 5, 15);
  text("Listening for OSC message /wek/outputs on port 12000", 5, 30);
  text("1 = Left   2 = Right   3 = Jump", 5, 45);
  
}

private void goLeft() {
  left = true;
  leftCount = 10;
}

private void goRight() {
  right = true;
  rightCount = 10;
}

private void jump() {
  up = true;
}
class Player {
  int x,y;
  float xSpeed,ySpeed;
  float accel,deccel;
  float maxXspd,maxYspd;
  float xSave,ySave;
  int xRep,yRep;
  float gravity;
  Player(int _x, int _y ) {
    x = _x;
    y = _y;
    xSpeed = 0;
    ySpeed = 0;
    accel = 0.5f;
    deccel = 0.5f;
    maxXspd = 2;
    maxYspd = 12;
    xSave = 0;
    ySave = 0;
    xRep = 0;
    yRep = 0;
    gravity = 0.25f;
  }
  public void update() {
    if ( right ) {
      xSpeed += accel;
      if ( xSpeed > maxXspd ) {
        xSpeed = maxXspd;
      }
    }
    else if ( left ) {
      xSpeed -= accel;
      if ( xSpeed < -maxXspd ) {
        xSpeed = -maxXspd;
      }
    }
    else { //neither right or left pressed, decelerate
      if ( xSpeed > 0 ) {
        xSpeed -= deccel;
        if ( xSpeed < 0 ) {
          xSpeed = 0;
        }
      }
      else if ( xSpeed < 0 ) {
        xSpeed += deccel;
        if ( xSpeed > 0 ) {
          xSpeed = 0;
        }
      }
    }
    
    if ( up ) {
      if ( !place_free(x,y+16) || !place_free(x+15,y+16) ) {
        ySpeed = -5.3f;
      }
    }
    
    ySpeed += gravity;
    
    /*
    // The technique used for movement involves taking the integer (without the decimal)
    // part of the player's xSpeed and ySpeed for the number of pixels to try to move,
    // respectively.  The decimal part is accumulated in xSave and ySave so that once
    // they reach a value of 1, the player should try to move 1 more pixel.  This jump
    // is not normally visible if it is moving fast enough.  This method is used because
    // is guarantees that movement is pixel perfect because the player's position will
    // always be at a whole number.  Whole number positions prevents problems when adding
    // new elements like jump through blocks or slopes.
    */
    xRep = 0; //should be zero because the for loops count it down but just as a safety
    yRep = 0;
    xRep += floor(abs(xSpeed));
    yRep += floor(abs(ySpeed));
    xSave += abs(xSpeed)-floor(abs(xSpeed));
    ySave += abs(ySpeed)-floor(abs(ySpeed));
    int signX = (xSpeed<0) ? -1 : 1;
    int signY = (ySpeed<0) ? -1 : 1;
    //when the player is moving a direction collision is tested for only in that direction
    //the offset variables are used for this in the for loops below
    int offsetX = (xSpeed<0) ? 0 : 15;
    int offsetY = (ySpeed<0) ? 0 : 15;
    
    if ( xSave >= 1 ) {
      xSave -= 1;
      xRep++;
    }
    if ( ySave >= 1 ) {
      ySave -= 1;
      yRep++;
    }
    
    for ( ; yRep > 0; yRep-- ) {
      if ( place_free(x,y+offsetY+signY) && place_free(x+15,y+offsetY+signY) ) {
        y += signY;
      }
      else {
        ySpeed = 0;
      }
    }
    for ( ; xRep > 0; xRep-- ) {
      if ( place_free(x+offsetX+signX,y) && place_free(x+offsetX+signX,y+15) ) {
        x += signX;
      }
      else {
        xSpeed = 0;
      }
    }
      
  }
  public void show() {
    pushMatrix();
    fill(255,0,0);
    noStroke();
    rect(x,y,16,16);
    popMatrix();
  }
}
  public void settings() {  size(480, 368); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Output" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
