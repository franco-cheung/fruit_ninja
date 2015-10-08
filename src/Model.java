/**
 * CS349 Winter 2014
 */
import java.util.ArrayList;
import java.util.Vector;

/*
 * Class the contains a list of fruit to display.
 * Follows MVC pattern, with methods to add observers,
 * and notify them when the fruit list changes.
 */
public class Model {
  // Observer list
  private Vector<ModelListener> views = new Vector();

  // Fruit that we want to display
  private ArrayList<Fruit> shapes = new ArrayList();
  private int score = 0;
  private int miss = 0;
  private long timer;
  private int highscore;
  private String stop_time = "0:00";
  public boolean stop_timer = false;
  public long global_time = 0;
  // Constructor
  Model() {
    shapes.clear();
    resetTime();
  }

  // MVC methods
  // These likely don't need to change, they're just an implementation of the
  // basic MVC methods to bind view and model together.
  public void addObserver(ModelListener view) {
    views.add(view);
  }

  public void notifyObservers() {
    for (ModelListener v : views) {
      v.update();
    }
  }

  // Model methods
  // You may need to add more methods here, depending on required functionality.
  // For instance, this sample makes to effort to discard fruit from the list.
  public void add(Fruit s) {
    shapes.add(s);
    notifyObservers();
  }

  public void remove (Fruit s){
	  shapes.remove(s);
	  notifyObservers();
  }
  
  public ArrayList<Fruit> getShapes() {
      return (ArrayList<Fruit>)shapes.clone();
  }
  
  public void scoreboard (){
	  score += 100;
	  notifyObservers();
  }
  
  public void setscore (){
	  score = 0;
	  notifyObservers();
  }
  
  public int getscore (){
	  return score;
  }
  
  public void missed(){
	  miss += 1;
	  if(miss > 5)
	  {
		  stop_timer = true;
	  }
	  notifyObservers();
  }
  
  public void setmissed(){
	  miss = 0;
	  notifyObservers();
  }
  
  public int getmissed(){
	  return miss;
  }
  
  public void setHighscore(int value)
  {
	  highscore = value;
	  notifyObservers();
  }
  
  public int getHighscore()
  {
	  return highscore;
  }
  
  public String Elapsed(){
	  long cur_time = System.currentTimeMillis();
	  long timeNow = cur_time - global_time;
	  String time_display;
	  if(!stop_timer)
	  {
		  time_display = String.valueOf(((timeNow/1000)/60) % 60) + " : " + String.valueOf((timeNow/1000) % 60);
		  stop_time = time_display;
	  }
	  return stop_time;
  }

  public void resetTime(){
	  global_time = System.currentTimeMillis();
  }
}
