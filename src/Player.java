import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;

public class Player {

  private PApplet sketch;
  private PVector pos;
  private int shootCount = 0;
  private final int SHOOT_COUNT_START = 20;
  public int score = 0;
  public float fitness = 0;
  public NN brain;
  private ArrayList<Laser> lasers = new ArrayList<Laser>();
  private boolean isLasering;
  private int size;
  private float heading;
  private PVector vel;
  private float rotation;
  private boolean isBoosting;
  float rate = .01f;
  private float[] inputs = new float[8];


  public Player(PApplet sketch) {
    this.sketch = sketch;
    pos = new PVector(sketch.width / 2, sketch.height / 2);
    size = 20;
    heading = 0;
    vel = new PVector();
    brain = new NN(sketch,33,8,4);
  }

  public Player(PApplet sketch ,NN brain) {
    this(sketch);
    this.brain = brain.copy();
  }

  public ArrayList<Laser> getLasers() {
    return lasers;
  }

  public int getSize() {
    return size;
  }

  public PVector getPos() {
    return pos;
  }

  public float getHeading() {
    return heading;
  }

  public PApplet getSketch() {
    return sketch;
  }

  public void drawShip() {
    sketch.push();
    sketch.translate(pos.x, pos.y);
    sketch.rotate(this.heading + (float) Math.PI / 2);
    sketch.noFill();
    sketch.stroke(255);
    sketch.triangle(-size, size, size, size, 0, -size);
    sketch.pop();
  }

  public void setRotation(float angle) {
    rotation = angle;
  }

  public void turn() {
    heading += rotation;
  }

  public void thrust() {
    PVector thrust = PVector.fromAngle(heading);
    thrust.mult(0.1f);
    vel.add(thrust);
  }

  public void update() {
//    score++;
    shootCount--;
    if (this.isBoosting) {
      thrust();
    }
    if (this.isLasering) {
      laser();
    }
    this.pos.add(this.vel);
    this.vel.mult(.99f);
  }

  public void thrusting(boolean b) {
    isBoosting = b;
  }

  public void edges() {
    if (pos.x > sketch.width + size) {
      pos.x = -size;
    } else if (pos.x < -size) {
      pos.x = sketch.width + size;
    }

    if (pos.y > sketch.height + size) {
      pos.y = -size;
    } else if (pos.y < -size) {
      pos.y = sketch.height + size;
    }
  }

  public void lasering(boolean b) {
    isLasering = b;
  }

  public void laser() {
    if (shootCount <= 0) {
      lasers.add(new Laser(sketch, pos, heading, size));
      shootCount = SHOOT_COUNT_START;
    }
  }

  public void mutate() {
    brain.mutate(rate);
  }

  public void look(ArrayList<Asteroid> asteroids){

    inputs = new float[33];
    PVector direction;
    for(int i = 0; i < inputs.length - 1; i += 2){
      direction = PVector.fromAngle((float) (rotation + i*(Math.PI / 8)));
      direction.mult(10);
      lookInDirection(direction, i, asteroids);
    }

    if(shootCount <= 0 && inputs[0] != 0){
      inputs[32] = 1;
    }else {
      inputs[32] = 0;
    }
  }

  private float lookInDirection(PVector direction, int i, ArrayList<Asteroid> asteroids) {
    PVector position = new PVector(pos.x,pos.y);
    float distance = 0;

    position.add(direction);
    distance += 1;
    PVector looped = new PVector(0,0);
    while(distance < 60){
      for(Asteroid a : asteroids){
        if(a.hitByLaser(position)){
          inputs[i] = 1/distance;
          Asteroid asteroidHit = a.getAsteroid(position);

          PVector towardsPlayer = new PVector(pos.x - asteroidHit.getPos().x - looped.x, pos.y - asteroidHit.getPos().y - looped.x);
          towardsPlayer.normalize();
          float redShift = asteroidHit.getVel().dot(towardsPlayer);
          inputs[i + 1]= redShift;

        }
      }

      position.add(direction);

      if (position.y < -50) {
        position.y += sketch.height + 100;
        looped.y += sketch.height + 100;
      } else
      if (position.y > sketch.height + 50) {
        position.y -= sketch.height -100;
        looped.y -= sketch.height + 100;
      }
      if (position.x< -50) {
        position.x += sketch.width +100;
        looped.x += sketch.width + 100;
      } else  if (position.x > sketch.width + 50) {
        position.x -= sketch.width +100;
        looped.x -= sketch.width + 100;
      }


      distance +=1;
    }

    return 0f;
  }

  public void think(ArrayList<Asteroid> asteroids) {
//    Asteroid closest = null;
//    float recordD = Float.POSITIVE_INFINITY;
//    for(int i = 0; i < asteroids.size(); i++){
//      Asteroid a = asteroids.get(i);
//      float d = PVector.dist(a.getPos(),pos);
//      if(d <recordD && d > 0){
//        closest = asteroids.get(i);
//        recordD = d;
//      }
//    }
//
//    inputs[0] = closest.getRadius();
//    inputs[1] = closest.getPos().x;
//    inputs[2] = closest.getPos().y;
//    inputs[3] = pos.x;
//    inputs[4] = pos.y;
//    inputs[5] = vel.x;
//    inputs[6] = vel.y;

    look(asteroids);
    float[] guess = brain.feedForward(inputs);

    if(guess[0] > .8){
      thrusting(true);
    }else {
      thrusting(false);
    }

    if(guess[1] > .8){
      setRotation(-.1f);
    }else{
      if(guess[2] > .8){
        setRotation(.1f);
      }else{
        setRotation(0);
      }
    }

    if(guess[3] > .8){
      lasering(true);
    }else{
      lasering(false);
    }
  }
}
