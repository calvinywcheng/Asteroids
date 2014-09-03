import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Bullet extends BaseShape {

	private Rectangle2D.Double bullet = new Rectangle2D.Double();

	public Bullet(double xpos, double ypos, int width, int height, double angle, double veloc){
		super(xpos, ypos, width, height, angle, veloc);
	}

	public boolean intersects(double xpos, double ypos, double width, double height){
		return bullet.intersects(xpos, ypos, width, height);
	}

	public void paint(Graphics2D g){

		bullet = new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());

		g.rotate(getAngle(), getX()+getWidth()/2, getY()+getHeight()/2);
		g.fill(bullet);

		g.rotate(-getAngle(), getX()+getWidth()/2, getY()+getHeight()/2);
	}
}