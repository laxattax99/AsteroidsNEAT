import java.util.ArrayList;
import java.util.Random;
import processing.core.PApplet;

public class GA {

  public static int Gens;
  public static int TOTAL;
  public static ArrayList<Player> currentPopulation;
  public static ArrayList<Player> savedPopulation;
  public static PApplet baseSketch;

   static void nextGen(){
    Gens++;
     System.out.println("Generation " + Gens);
     calculateFitness();
     for(int i = 0; i < TOTAL; i++){
       currentPopulation.add(pickOne());
     }
     savedPopulation.clear();
  }

  private static Player pickOne() {
     int index = 0;
//    Random rand = new Random();
     float r = baseSketch.random(1);//rand.nextInt(1);
     while (r >0){
       r -= savedPopulation.get(index).fitness;
       index++;
     }
     index--;
     Player p = savedPopulation.get(index);
     Player child = new Player(p.getSketch(),p.brain);
     child.mutate();
     return child;
  }

  private static void calculateFitness() {
     float sum = 0;
     for(Player p: savedPopulation){
       sum+= p.score;
     }

     for(int i = 0; i < savedPopulation.size(); i++){
       Player p = savedPopulation.get(i);
       savedPopulation.get(i).fitness = savedPopulation.get(i).score / sum;
     }
  }

}
