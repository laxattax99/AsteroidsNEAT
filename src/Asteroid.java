import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import processing.core.PApplet;
import processing.core.PVector;

public class Asteroid {

  private PApplet sketch;
  private PVector pos;
  private float radius;

  public PVector getPos() {
    return pos;
  }

  public float getRadius() {
    return radius;
  }

  public PVector getVel() {
    return vel;
  }

  private PVector vel;

  public Asteroid(PApplet sketch) {
    this.sketch = sketch;
    this.radius = sketch.random(15, 75);
//    this.pos = new PVector(sketch.random(sketch.width / 3), sketch.random(sketch.height / 3));
    int spawnEdge = (int) sketch.random(4);
    if(spawnEdge == 0){
      pos = new PVector(sketch.random(sketch.width),0);
    }else if(spawnEdge == 1){
      pos = new PVector(sketch.width, sketch.random(sketch.height));
    }else if(spawnEdge == 2){
      pos = new PVector(sketch.random(sketch.width),sketch.height);
    }else{
      pos = new PVector(0,sketch.random(sketch.height));
    }

//    System.out.println(pos.x + "    " + pos.y);

    this.vel = PVector.random2D();
    vel = getVelFromRadius(radius, vel);
  }

  private PVector getVelFromRadius(float radius, PVector vel) {
    if (radius <= 30) {
      vel.mult(4.5f);
    } else if (radius > 30 && radius <= 50) {
      vel.mult(3f);
    } else {
      vel.mult(2f);
    }

    return vel;
  }

  public Asteroid(PApplet sketch, PVector pos, float radius) {
    this.sketch = sketch;
    this.pos = pos;
    this.radius = radius;
    this.vel = PVector.random2D();
    this.vel = getVelFromRadius(radius, this.vel);
  }

  public void drawAsteroid() {
//    sketch.rotate(this.heading + (float)Math.PI / 2);
    sketch.push();
    sketch.translate(pos.x, pos.y);
    sketch.noFill();
    sketch.stroke(255);
    sketch.ellipse(0, 0, radius * 2, radius * 2);
    sketch.pop();
  }

  public void update() {
    pos.add(vel);
  }

  public void edges() {
    if (pos.x > sketch.width + radius) {
      pos.x = -radius;
    } else if (pos.x < -radius) {
      pos.x = sketch.width + radius;
    }

    if (pos.y > sketch.height + radius) {
      pos.y = -radius;
    } else if (pos.y < -radius) {
      pos.y = sketch.height + radius;
    }
  }

  public ArrayList<Asteroid> divide() {
    ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    float newR = radius / 2;
    Asteroid a1 = new Asteroid(sketch, pos.copy(), newR);
    Asteroid a2 = new Asteroid(sketch, pos.copy(), newR);

    asteroids.add(a1);
    asteroids.add(a2);

    return asteroids;
  }

  public boolean hitPlayer(Player p) {
    float distance = PVector.dist(p.getPos(), pos);
    return (distance < p.getSize() + radius);
  }

  public boolean hitByLaser(PVector laserPos){
    float distance = PVector.dist(laserPos,pos);
    return (distance < radius);
  }

  public Asteroid getAsteroid(PVector bulletPos){
    if (PVector.dist(pos, bulletPos)< radius) {
      return this;
    }
    if (pos.x< -50 +radius || pos.x > sketch.width+50 - radius || pos.y< -50 + radius || pos.y > sketch.height+50 -radius ) {//if ateroid is overlapping edge
      if (pos.x< -50 +radius || pos.x > sketch.width+50 - radius || pos.y< -50 + radius || pos.y > sketch.height+50 -radius ) {//if bullet is near the edge
        PVector overlapPos = new PVector(pos.x, pos.y);
        if (pos.x< -50 +radius) {
          overlapPos.x += sketch.width+100;
        }
        if ( pos.x > sketch.width+50 - radius ) {
          overlapPos.x -= sketch.width+100;
        }

        if ( pos.y< -50 + radius) {
          overlapPos.y +=sketch.height + 100;
        }

        if (pos.y > sketch.height+50 -radius) {

          overlapPos.y -= sketch.height + 100;
        }
        if (PVector.dist(overlapPos, bulletPos)< radius) {
          return this;
        }
      }
    }
    return null;
  }
}
