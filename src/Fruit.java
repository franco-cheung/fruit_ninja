/**
 * CS349 Winter 2014
 */
import java.awt.*;
import java.awt.geom.*;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit implements FruitInterface {
    private Area            fruitShape   = null;
    private Color           fillColor    = Color.RED;
    private Color           outlineColor = Color.BLACK;
    private AffineTransform transform    = new AffineTransform();
    private double          outlineWidth = 5;
    public boolean			cut	= false;
    public boolean			scored = false;
    public double			velocity;
    public double			gravity = 0.55;

    /**
     * A fruit is represented using any arbitrary geometric shape.
     */
    Fruit (Area fruitShape, boolean sliced, boolean scored) {
        this.fruitShape = (Area)fruitShape.clone();
        this.cut = sliced;
        this.scored = scored;
    }

    /**
     * The color used to paint the interior of the Fruit.
     */
    public Color getFillColor() {
        return fillColor;
    }
    /**
     * The color used to paint the interior of the Fruit.
     */
    public void setFillColor(Color color) {
        fillColor = color;
    }
    /**
     * The color used to paint the outline of the Fruit.
     */
    public Color getOutlineColor() {
        return outlineColor;
    }
    /**
     * The color used to paint the outline of the Fruit.
     */
    public void setOutlineColor(Color color) {
        outlineColor = color;
    }
    
    /**
     * Gets the width of the outline stroke used when painting.
     */
    public double getOutlineWidth() {
        return outlineWidth;
    }

    /**
     * Sets the width of the outline stroke used when painting.
     */
    public void setOutlineWidth(double newWidth) {
        outlineWidth = newWidth;
    }

    /**
     * Concatenates a rotation transform to the Fruit's affine transform
     */
    public void rotate(double theta) {
        transform.rotate(theta);
    }

    /**
     * Concatenates a scale transform to the Fruit's affine transform
     */
    public void scale(double x, double y) {
        transform.scale(x, y);
    }

    /**
     * Concatenates a translation transform to the Fruit's affine transform
     */
    public void translate(double tx, double ty) {
    	transform.translate(tx, ty);
    }

    /**
     * Returns the Fruit's affine transform that is used when painting
     */
    public AffineTransform getTransform() {
        return (AffineTransform)transform.clone();
    }

    /**
     * Creates a transformed version of the fruit. Used for painting
     * and intersection testing.
     */
    public Area getTransformedShape() {
        return fruitShape.createTransformedArea(transform);
    }

    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Graphics2D g2) {
        // TODO BEGIN CS349
    	//Draw uncut fruits
    	g2.setColor(Color.red);
    	if(!this.cut)
    	{
    		g2.fill(this.getTransformedShape());
    	}
    	else
    	{
    		g2.setColor(Color.darkGray);
    		g2.fill(this.getTransformedShape());
    	}
    	
        // TODO END CS349
    }

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    public boolean intersects(Point2D p1, Point2D p2) {
        // TODO BEGIN CS349
    	
    	if(this.contains(p1) || this.contains(p2) || this.cut)
    	{
    		return false;
    	}

    	return this.getTransformedShape().getBounds().intersectsLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    	
        // TODO END CS349
        
    }

	/**
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(Point2D p1) {
        return this.getTransformedShape().contains(p1);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] split(Point2D p1, Point2D p2) throws NoninvertibleTransformException {
        Area topArea = (Area)this.getTransformedShape().clone();
        Area bottomArea = (Area)this.getTransformedShape().clone();
        AffineTransform area_transform = new AffineTransform();
        
        // TODO BEGIN CS349
        double slope, constant, center_x, center_y, radius, rotation;
        double delta, quad_a, quad_b, quad_c, x1_coord, y1_coord, x2_coord, y2_coord;
        Point2D midpoint;
        
        //creating rectangles
        Rectangle top_rec;
        Area top_area;
        Rectangle bottom_rec;
        Area bottom_area;
        
        //finding slope of the line
        slope = (p2.getY() - p1.getY())/(p2.getX() - p1.getX());
        
        //a line is y = mx+c, so we are finding c in this case
        constant = p1.getY() - (slope * p1.getX());
        
        //getting the center of the circle
        center_x = this.getTransformedShape().getBounds().getCenterX();
        center_y = this.getTransformedShape().getBounds().getCenterY();
    
        //getting radius of the circle
        radius = this.getTransformedShape().getBounds().getWidth()/2;
        
        //calculating the values for the quadratic formula
        quad_a = (1 + Math.pow(slope, 2));
        quad_b = 2 * slope * (constant - center_y) - 2 * center_x;
        quad_c = Math.pow(center_x, 2) + Math.pow((constant - center_y), 2) - Math.pow(radius, 2);
        delta = Math.pow(quad_b, 2) - 4 * quad_a * quad_c;
        
        //if the cut was within the rectangle, but does not pass the circle
        if(delta < 0)
        {
        	return new Fruit[0];
        }
        
        //calculate the intersection points between the line and the circle
        x1_coord = (-quad_b + Math.sqrt(delta)) / (2 * quad_a);
        y1_coord = slope * x1_coord + constant;
        x2_coord = (-quad_b - Math.sqrt(delta)) / (2 * quad_a);
        y2_coord = slope * x2_coord + constant;

        
        midpoint = new Point2D.Double(((x1_coord + x2_coord)/2), ((y1_coord + y2_coord)/2));
        
        //rotate and translate
        double deltax = (x2_coord - x1_coord);
        double deltay = (y2_coord - y1_coord);
        rotation = Math.atan2(deltay,deltax);

        area_transform.rotate(-rotation);
        area_transform.translate(-midpoint.getX(), -midpoint.getY());
        topArea.transform(area_transform);
        bottomArea.transform(area_transform);
        
        double fruitwidth = this.getTransformedShape().getBounds().width;
        // create rectangle/area for intersect function
    	top_rec = new Rectangle((int)-radius*2, (int)-radius*2, (int)fruitwidth*2, (int)radius*2);
    	top_area = new Area(top_rec);
    	bottom_rec = new Rectangle((int)-radius*2, 0, (int)fruitwidth*2, (int)radius*2);
    	bottom_area = new Area(bottom_rec);
    	
        //bisect
        topArea.intersect(top_area);
        bottomArea.intersect(bottom_area);
        
        //transform back the area to the correct position
        topArea.transform(area_transform.createInverse());
        bottomArea.transform(area_transform.createInverse());
        
        //create the fruit
        Fruit topFruit = new Fruit(topArea, true, true);
        Fruit bottomFruit = new Fruit(bottomArea, true, true);
        
        //clear old fruit and indicate that it has been cut
    	this.cut = true;
        fruitShape.reset();
        
        // Rotate shape to align slice with x-axis
        // Bisect shape above/below x-axis (look at intersection methods!)
        // TODO END CS349
        
        return new Fruit[] { topFruit, bottomFruit };
        /*
        if (topArea != null && bottomArea != null)
            return new Fruit[] { topFruit, bottomFruit };
        return new Fruit[0];
        */
     }
}
