import processing.core.PApplet;
import processing.core.PVector;

public class Laser {

  PApplet sketch;
  PVector pos;
  PVector vel;
  int offset;

  public Laser(PApplet sketch, PVector shipPos, float shipHeading, int offset) {
    this.sketch = sketch;
    pos = new PVector((float) (shipPos.x + offset * Math.cos(shipHeading)),
        (float) (shipPos.y + offset * Math.sin(shipHeading)));
    vel = PVector.fromAngle(shipHeading);
    vel.mult(5);
    this.offset = offset;
  }

  public void update() {
    pos.add(vel);
  }

  public void renderLaser() {
    sketch.push();
    sketch.stroke(255);
    sketch.strokeWeight(4);
    sketch.point(pos.x, pos.y);
    sketch.pop();
  }

  public boolean hits(Asteroid a) {
    //(float) Math.sqrt(Math.pow(a.getPos().x - pos.x, 2) + Math.pow(a.getPos().y - pos.y, 2));
    float distance = PVector.dist(a.getPos(), pos);
    return (distance < a.getRadius());
  }

  public boolean offScreen(){
    if(pos.x > sketch.width || pos.x <  0) {
      return true;
    }

    if(pos.y > sketch.height || pos.y <  0) {
      return true;
    }
    return false;
  }

}
