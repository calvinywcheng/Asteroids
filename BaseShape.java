import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.awt.Graphics2D;

public abstract class BaseShape {

	private double xpos;
	private double ypos;
	private int width;
	private int height;
	private double angle;
	private double veloc;

	public BaseShape(double xpos, double ypos, int width, int height){
		this(xpos, ypos, width, height, toRadians(-90), 0);
	}

	public BaseShape(double xpos, double ypos, int width, int height, double angle, double veloc){

		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.angle = angle;
		this.veloc = veloc;
	}

	public void move(){
		xpos += veloc*cos(angle);
		ypos += veloc*sin(angle);
	}

	public void checkBounds(double appletWidth, double appletHeight){
		if(xpos>appletWidth){
			setPos(-width, ypos);
		}else if(xpos+width<0){
			setPos(appletWidth, ypos);
		}

		if(ypos>appletHeight){
			setPos(xpos, -height);
		}else if(ypos+height<0){
			setPos(xpos, appletHeight);
		}
	}

	abstract boolean intersects(double x, double y, double width, double height);

	public void changeAngle(double theta){

		angle+=toRadians(theta);
	}

	public void changeVel(double amount){

		veloc+=amount;

		if(veloc<0){
			veloc=0;
		}else if(veloc>5){
			veloc=5;
		}
	}

	public void setPos(double xpos, double ypos){
		this.xpos = xpos;
		this.ypos = ypos;
	}

	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
	}

	public void setAngle(double angle){
		this.angle = angle;
	}

	public void setVelocity(double veloc){
		this.veloc = veloc;
	}

	public double getX() { return xpos; }
	public double getY() { return ypos; }
	public int getWidth() { return width; }
	public int getHeight() { return height;	}
	public double getAngle() { return angle; }
	public double getVelocity() { return veloc; }

	abstract void paint(Graphics2D g);
}