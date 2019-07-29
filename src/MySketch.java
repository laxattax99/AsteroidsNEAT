import java.util.ArrayList;
import processing.core.PApplet;

public class MySketch extends PApplet {

  Player shipBest;
  ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
  //  ArrayList<Laser> lasers = new ArrayList<Laser>();
  int totalAsteroids;


  public void settings() {
    this.size(1280, 960);
//    ship = new Player(this);
    totalAsteroids = 10;
    addAsteroids();
    GA.TOTAL = 500;
    GA.currentPopulation = new ArrayList<Player>();
    GA.savedPopulation = new ArrayList<Player>();
    GA.baseSketch = this;
    for (int i = 0; i < GA.TOTAL; i++) {
      GA.currentPopulation.add(new Player(this));
    }

  }

  private void addAsteroids() {
    for (int i = 0; i < totalAsteroids; i++) {
      asteroids.add(new Asteroid(this));
    }
  }


//  @Override
//  public void keyReleased() {
//    ship.setRotation(0);
//    ship.thrusting(false);
//    ship.lasering(false);
//  }
//
//  @Override
//  public void keyPressed() {
//    if (keyCode == 37) {
//      ship.setRotation(-.1f);
//    } else if (keyCode == 39) {
//      ship.setRotation(.1f);
//    } else if (keyCode == 38) {
//      ship.thrusting(true);
//    } else if (key == ' ') {
//      System.out.println(ship.getLasers().size());
////      lasers.add(new Laser(this, ship.getPos(), ship.getHeading(), ship.getSize()));
//      ship.lasering(true);
//    }
//  }

  public void draw() {
    background(10);

    shipBest = new Player(this);
    for (Player p : GA.savedPopulation) {
      if (p.score > shipBest.score) {
        shipBest = p;
      }
    }

    playGameShow(shipBest, asteroids);

    for (int i = 0; i < GA.currentPopulation.size(); i++) {
      playGame(GA.currentPopulation.get(i), asteroids, i);
    }

//    for (int i = 0; i < GA.currentPopulation.size(); i++) {
//
//    }
//
//    for (Asteroid a : asteroids) {
//      a.drawAsteroid();
//      a.update();
//      a.edges();
//      for (int i = 0; i < GA.currentPopulation.size(); i++) {
//        Player ship = GA.currentPopulation.get(i);
//        if (a.hitPlayer(ship)) {
//          GA.savedPopulation.add(ship);
//          GA.currentPopulation.remove(i);
//        }
//      }
//    }
//

    if (GA.currentPopulation.size() == 0) {
      GA.nextGen();
      asteroids.clear();
      addAsteroids();
    }
//
//    for (Player ship : GA.currentPopulation) {
//      ship.drawShip();
//      ship.turn();
//      ship.update();
//      ship.edges();
//      ship.think(asteroids);
//    }
//
//    for (Player ship : GA.currentPopulation) {
//      ArrayList<Laser> lasers = ship.getLasers();
//      for (int j = lasers.size() - 1; j >= 0; j--) {
//        Laser laser = lasers.get(j);
//        laser.renderLaser();
//        laser.update();
//
//        if (laser.offScreen()) {
//          lasers.remove(j);
//        } else {
//          for (int i = asteroids.size() - 1; i >= 0; i--) {
//            Asteroid a = asteroids.get(i);
//            if (laser.hits(a)) {
//              ArrayList<Asteroid> newAsteroids = a.divide();
//              asteroids.remove(i);
//              lasers.remove(j);
//              if (newAsteroids.get(0).getRadius() >= 15) {
//                asteroids.addAll(newAsteroids);
//              }
//            }
//          }
//        }
//
//      }
//    }
//
//    if (asteroids.size() == 0) {
//      totalAsteroids += 2;
//      addAsteroids();
//    }

  }

  public void playGame(Player ship, ArrayList<Asteroid> asteroids, int currentIdx) {
//    while(true) {
    for (Asteroid a : asteroids) {
//      a.drawAsteroid();
      a.update();
      a.edges();

      if (a.hitPlayer(ship)) {
        GA.savedPopulation.add(ship);
        GA.currentPopulation.remove(currentIdx);
        return;
      }
    }

//    ship.drawShip();
    ship.turn();
    ship.update();
    ship.edges();
    ship.think(asteroids);

    ArrayList<Laser> lasers = ship.getLasers();
    for (int j = lasers.size() - 1; j >= 0; j--) {
      Laser laser = lasers.get(j);
//      laser.renderLaser();
      laser.update();

      if (laser.offScreen()) {
        lasers.remove(j);
      } else {
        for (int i = asteroids.size() - 1; i >= 0; i--) {
          Asteroid a = asteroids.get(i);
          if (laser.hits(a)) {
            if (a.getRadius() <= 30) {
              ship.score += 100;
            } else if (a.getRadius() > 30 && a.getRadius() <= 50) {
              ship.score += 50;
            } else {
              ship.score += 20;
            }
            ArrayList<Asteroid> newAsteroids = a.divide();
            asteroids.remove(i);
            if (lasers.size() > j) {
              lasers.remove(j);
            }
            if (newAsteroids.get(0).getRadius() >= 15) {
              asteroids.addAll(newAsteroids);
            }
          }
        }
      }

    }

    if (asteroids.size() == 0) {
      totalAsteroids += 2;
      addAsteroids();
    }
//    }
  }

  public void playGameShow(Player ship, ArrayList<Asteroid> asteroids) {
//    while(true) {
    for (Asteroid a : asteroids) {
      a.drawAsteroid();
      a.update();
      a.edges();

      if (a.hitPlayer(ship)) {
        return;
      }
    }

    ship.drawShip();
    ship.turn();
    ship.update();
    ship.edges();
    ship.think(asteroids);

    ArrayList<Laser> lasers = ship.getLasers();
    for (int j = lasers.size() - 1; j >= 0; j--) {
      Laser laser = lasers.get(j);
      laser.renderLaser();
      laser.update();

      if (laser.offScreen()) {
        lasers.remove(j);
      } else {
        for (int i = asteroids.size() - 1; i >= 0; i--) {
          Asteroid a = asteroids.get(i);
          if (laser.hits(a)) {
            if (a.getRadius() <= 30) {
              ship.score += 100;
            } else if (a.getRadius() > 30 && a.getRadius() <= 50) {
              ship.score += 50;
            } else {
              ship.score += 20;
            }
            ArrayList<Asteroid> newAsteroids = a.divide();
            asteroids.remove(i);
            if (lasers.size() > j) {
              lasers.remove(j);
            }
            if (newAsteroids.get(0).getRadius() >= 15) {
              asteroids.addAll(newAsteroids);
            }
          }
        }
      }

    }

    if (asteroids.size() == 0) {
      totalAsteroids += 2;
      addAsteroids();
    }
//    }
  }

  public static void main(String[] args) {
    String[] processingArgs = {"MySketch"};
    MySketch mySketch = new MySketch();
    PApplet.runSketch(processingArgs, mySketch);
  }
}
