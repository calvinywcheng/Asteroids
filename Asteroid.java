import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Asteroid extends BaseShape {

	private Ellipse2D.Double c = new Ellipse2D.Double();

	public Asteroid(double xpos, double ypos, int width, int height, double angle, double veloc){
		super(xpos, ypos, width, height, angle, veloc);
	}

	public boolean intersects(double xpos, double ypos, double width, double height){
		return c.intersects(xpos, ypos, width, height);
	}

	public void paint(Graphics2D g){

		c = new Ellipse2D.Double(getX(), getY(), getWidth(), getHeight());

		g.rotate(getAngle(), getX()+getWidth()/2, getY()+getHeight()/2);
		g.fill(c);

		g.rotate(-getAngle(), getX()+getWidth()/2, getY()+getHeight()/2);
	}
}