/**
 * CS349 Winter 2014
 */
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * View to display the Title, and Score
 * Score currently just increments every time we get an update
 * from the model (i.e. a new fruit is added).
 */
public class TitleView extends JPanel implements ModelListener {
  private Model model;
  private JLabel title, score, missed, timer;
  private String display_time;
  private int count = 0;
  private int miss = 0;
  


  // Constructor requires model reference
  TitleView (Model model) {
    // register with model so that we get updates
    this.model = model;
    this.model.addObserver(this);
	
    // draw something
    setBorder(BorderFactory.createLineBorder(Color.black));
    setBackground(Color.YELLOW);
    // You may want a better name for this game!
    title = new JLabel(" Fruit Slasher Ninja ");
    timer = new JLabel();
    score = new JLabel();
    missed = new JLabel();
    

    // use border layout so that we can position labels on the left and right
    this.setLayout(new BorderLayout());
    this.add(title, BorderLayout.NORTH);
    this.add(score, BorderLayout.WEST);
    this.add(timer, BorderLayout.SOUTH);
    this.add(missed, BorderLayout.EAST);
  }

  // Panel size
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500,50);
  }

  // Update from model
  // This is ONLY really useful for testing that the view notifications work
  // You likely want something more meaningful here.
  @Override
  public void update() {
	count = model.getscore();
	miss = model.getmissed();
	display_time = model.Elapsed();
    paint(getGraphics());
    
  }

  // Paint method
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    score.setText(" Score: " + count + "  ");
    missed.setText(" Missed: " + miss + "  ");
    timer.setText(" Elapsed Time " + display_time);
  }
}
