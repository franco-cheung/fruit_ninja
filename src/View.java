/**
 * CS349 Winter 2014
 */
import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/*
 * View of the main play area.
 * Displays pieces of fruit, and allows players to slice them.
 */
public class View extends JPanel implements ModelListener {
	private Model model;
	private JLabel finish = new JLabel();
	private JLabel score_result = new JLabel();
	private JLabel highscore_result = new JLabel();
	private JLabel reset = new JLabel();
	private final MouseDrag drag;
	private Timer spawn_t;
	private Timer t;
	private ArrayList<Fruit> fruitlist;
	private boolean stopped = false;
	
	// Constructor
	View (Model m) {
		model = m;
		model.addObserver(this);
		finish.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		finish.setVisible(false);
		reset.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
		reset.setVisible(false);

		setBackground(Color.WHITE);
		
		//action listerner to spawn the fruits at a certain rate
		ActionListener spawn = new ActionListener(){
			public void actionPerformed(ActionEvent animateEvent)
			{
				
				//generating random x position within the screen
				double x_pos = Math.random() * View.this.getHeight();
				double velocity = Math.random() * 5 + 15;
				Fruit f = new Fruit(new Area(new Ellipse2D.Double(0, 0, 50, 50)), false, false);
				f.translate(x_pos, View.this.getHeight());
				f.velocity = velocity * -1;
				model.add(f);

			}


		};
		spawn_t = new Timer(750, spawn);
		spawn_t.start();
		
		//action listener to animate the fruit going up and down
		ActionListener animate = new ActionListener(){
			public void actionPerformed(ActionEvent animateEvent)
			{
				fruitlist = model.getShapes();
				
				for(Fruit s: fruitlist)
				{
					//translating the fruit up/down
					s.translate(0, s.velocity);          
					s.velocity += s.gravity;
					//increment scoreboard
					if(s.cut && !s.scored && !stopped)
					{
						s.scored = true;
						model.scoreboard();
					}
					//if the fruit is off screen
					if(s.getTransformedShape().getBounds().y > View.this.getHeight() || s.getTransformedShape().getBounds().y == 0)
					{
						//increment the miss value
						if(!s.cut && !stopped)
						{
							model.missed();
							//stop spawning fruits once user hits more than 5 and indicate game over
							if(model.getmissed() > 5)
							{
								stopped = true;
								spawn_t.stop();
								finish.setVisible(true);
								finish.setText("Game Over!");
								finish.addMouseListener(mouseListener);
							}
						}
						//remove fruits that are off screen
						model.remove(s);
						
					}
					//update the graphics
					update();
				}
				//display result screen
				if(fruitlist.isEmpty() && stopped)
				{
					t.stop();
					//update highscore
					if(model.getHighscore() < model.getscore())
					{
						model.setHighscore(model.getscore());
					}
					score_result.setText("Score: " + model.getscore());
					highscore_result.setText("Highscore: " + model.getHighscore());
					reset.setText("Play Again!");
					finish.setVisible(false);
					score_result.setVisible(true);
					highscore_result.setVisible(true);
					reset.addMouseListener(mouseListener);
					reset.setVisible(true);

					
				}

			}
		};
		t = new Timer(100, animate);
		t.start();

		
		// add a couple of fruit instances for test purposes
		// in a real game, you want to spawn fruit in random locations from the bottom of the screen
		// we use ellipse2D for simple shapes, you might consider something more complex


		/*Fruit f2 = new Fruit(new Area(new Ellipse2D.Double(0, 0, 50, 50)), false, false);
        f2.translate(200, 200);
        model.add(f2);*/
		
		//label area
		this.add(finish, BorderLayout.CENTER);
		this.add(score_result, BorderLayout.NORTH);
		this.add(reset, BorderLayout.EAST);
		this.add(highscore_result, BorderLayout.WEST);
		
		// drag represents the last drag performed, which we will need to calculate the angle of the slice
		drag = new MouseDrag();
		// add mouse listener
		addMouseListener(mouseListener);
	}

	// Update fired from model
	@Override
	public void update() {
		this.repaint();
	}

	// Panel size
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500,400);
	}

	// Paint this panel
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// draw all pieces of fruit
		// note that fruit is responsible for figuring out where and how to draw itself
		for (Fruit s : model.getShapes()) {
			s.draw(g2);
		}
	}

	// Mouse handler
	// This does most of the work: capturing mouse movement, and determining if we intersect a shape
	// Fruit is responsible for determining if it's been sliced and drawing itself, but we still
	// need to figure out what fruit we've intersected.
	private MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			drag.start(e.getPoint());
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			 if(e.getSource() == reset){
				  model.setscore();
		          model.setmissed();
		          reset.setVisible(false);
		          score_result.setVisible(false);
		          highscore_result.setVisible(false);
		          finish.setVisible(false);
		          stopped = false;
		          model.stop_timer = false;
		          t.start();
		          spawn_t.start();
		          model.resetTime();
		         }
			super.mouseReleased(e);
			drag.stop(e.getPoint());

			// you could do something like this to draw a line for testing
			// not a perfect implementation, but works for 99% of the angles drawn

			/*int[] x = { (int) drag.getStart().getX(), (int) drag.getEnd().getX(), (int) drag.getEnd().getX(), (int) drag.getStart().getX()};
			int[] y = { (int) drag.getStart().getY()-1, (int) drag.getEnd().getY()-1, (int) drag.getEnd().getY()+1, (int) drag.getStart().getY()+1};
			model.add(new Fruit(new Area(new Polygon(x, y, x.length)), true, true));*/

			// find intersected shapes
			int offset = -5; // Used to offset new fruits
			for (Fruit s : model.getShapes()) {
				if (s.intersects(drag.getStart(), drag.getEnd())) {
					s.setFillColor(Color.RED);
					try {
						Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());

						// add offset so we can see them split - this is used for demo purposes only!
						// you should change so that new pieces appear close to the same position as the original piece
						for (Fruit f : newFruits) {
							f.translate(offset, offset);
							model.add(f);
							offset += -5;
						}
					} catch (Exception ex) {
						System.err.println("Caught error: " + ex.getMessage());
					}
				} else {
					s.setFillColor(Color.BLUE);
				}
			}
		}
	};

	/*
	 * Track starting and ending positions for the drag operation
	 * Needed to calculate angle of the slice
	 */
	private class MouseDrag {
		private Point2D start;
		private Point2D end;

		MouseDrag() { }

		protected void start(Point2D start) { this.start = start; }
		protected void stop(Point2D end) { this.end = end; }

		protected Point2D getStart() { return start; }
		protected Point2D getEnd() { return end;}

	}
}
