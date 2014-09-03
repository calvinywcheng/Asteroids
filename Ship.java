import java.awt.Graphics2D;
import java.awt.Polygon;

public class Ship extends BaseShape {

	private Polygon p = new Polygon();
	private int[] xpoints = new int[4];
	private int[] ypoints = new int[4];

	public Ship(double xpos, double ypos, int width, int height){
		super(xpos, ypos, width, height);
	}

	public boolean intersects(double x, double y, double width, double height){
		return p.intersects(x, y, width, height);
	}

	public void paint(Graphics2D g){

		xpoints[0] = (int)(getX());
		xpoints[1] = (int)(getX()+getWidth()/3);
		xpoints[2] = (int)(getX());
		xpoints[3] = (int)(getX()+getWidth());

		ypoints[0] = (int)(getY());
		ypoints[1] = (int)(getY()+getHeight()/2);
		ypoints[2] = (int)(getY()+getHeight());
		ypoints[3] = (int)(getY()+getHeight()/2);

		p = new Polygon(xpoints, ypoints, 4);

		g.rotate(getAngle(), getX()+getWidth()/2, getY()+getHeight()/2);
		g.fill(p);

		g.rotate(-getAngle(), getX()+getWidth()/2, getY()+getHeight()/2);
	}
}