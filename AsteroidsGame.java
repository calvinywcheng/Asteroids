import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;

import static java.lang.Math.*;

public class AsteroidsGame extends Panel implements KeyListener {
	
	public static final int WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 100);
	public static final int HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 200);
	private static final String controls = "Controls: Arrow keys to move, Space to fire";
	private static final long serialVersionUID = 1L;
	//double buffer
	private BufferedImage imageBuffer;
	private Graphics2D graphicsBuffer;
	private ScheduledExecutorService t;
	private ScheduledFuture<?> future;

	//objects
	private Ship ship;
	private List<Asteroid> asteroids = new ArrayList<Asteroid>(18);
	private List<Bullet> bullets = new ArrayList<Bullet>(30);

	//working variables
	private boolean angleRight = false;
	private boolean angleLeft = false;
	private boolean velUp = false;
	private boolean velDown = false;
	private boolean fireBullets = false;
	private boolean reloading = false;
	private byte bulletDelay = 0;
	private byte shipHealth = 100;
	private boolean won = false;
	private long startTime;
	private double totalTimeInSeconds;

	public AsteroidsGame() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setSize(WIDTH, HEIGHT);
	}
	
	public void init() {

		//double buffering
		imageBuffer = (BufferedImage)createImage(getWidth(), getHeight());
		graphicsBuffer = (Graphics2D) imageBuffer.getGraphics();

		//initialize ship
		ship = new Ship(getWidth()/2, getHeight()/2, 20, 20);

		//initialize asteroids
		for(int i=0; i<2; i++){
			asteroids.add(new Asteroid(random()*(getWidth()-64), -63, 64, 64, random()*180, random()*PI));					//top
			asteroids.add(new Asteroid(random()*(getWidth()-64), getHeight()-1, 64, 64, random()*180+180, random()*PI));		//bottom
		}
		asteroids.add(new Asteroid(-63, random()*(getHeight()-64), 64, 64, random()*180-90, random()*PI));
		asteroids.add(new Asteroid(getWidth()-1, random()*(getHeight()-64), 64, 64, random()*180+90, random()*PI));

		//initialize bullets
		for(int i=0; i<30; i++){
			bullets.add(new Bullet(Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 5, ship.getAngle(), 0));
		}
		
		startTime = System.currentTimeMillis() - 100;
		
		addKeyListener(this);
		setFocusable(true);

		Runnable game = new TimerTask() {
			public void run(){
				Graphics g = getGraphics();

				//get everything moving
				ship.move();
				for(Asteroid rock : Collections.synchronizedList(asteroids)){
					rock.move();
				}
				for(Bullet bullet : Collections.synchronizedList(bullets)){
					bullet.move();
				}

				//bounds checking
				ship.checkBounds(getWidth(), getHeight());
				for(Asteroid rock : Collections.synchronizedList(asteroids)){
					rock.checkBounds(getWidth(), getHeight());
				}

				//boolean values to control movement
				if(angleRight){
					ship.changeAngle(3);
				}else if(angleLeft){
					ship.changeAngle(-3);
				}
				if(velUp){
					ship.changeVel(0.1);
				}else if(velDown){
					ship.changeVel(-0.05);
				}

				//drag force
				ship.changeVel(-0.02);

				//ship collision with asteroid
				for(int i=0; i<asteroids.size(); i++){
					if(ship.intersects(asteroids.get(i).getX(), asteroids.get(i).getY(), asteroids.get(i).getWidth(), asteroids.get(i).getHeight())){
						if(asteroids.get(i).getWidth()==64){
							shipHealth-=99;
						}else if(asteroids.get(i).getWidth()==32){
							shipHealth-=50;
						}else if(asteroids.get(i).getWidth()==16){
							shipHealth-=25;
						}else if(asteroids.get(i).getWidth()==8){
							shipHealth-=10;
						}
						asteroids.remove(i);
					}
				}

				//check when dead
				if(shipHealth<=0){
					future.cancel(false);
				}

				//check when won
				if(asteroids.size()==0){
					won = true;
					totalTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
					future.cancel(false);
				}

				//fire bullets from ship
				if(fireBullets && !reloading){
					//check to see if the bullet is already being fired.  if not, fire it
					for(int i=0; i<bullets.size(); i++){
						if(bullets.get(i).getVelocity()==0){
							bullets.get(i).setPos(ship.getX()+ship.getWidth()/2-3, ship.getY()+ship.getHeight()/2-3);
							bullets.get(i).setAngle(ship.getAngle());
							bullets.get(i).setVelocity(ship.getVelocity()+5);
							reloading = true;
							break;
						}
					}
				}
				if(bulletDelay>20){
					reloading = false;
					bulletDelay=0;
				}else{
					bulletDelay++;
				}

				//reset bullets
				for(int i=0; i<bullets.size(); i++){
					//bullets out of map
					if((bullets.get(i).getX()+bullets.get(i).getWidth()<0 || bullets.get(i).getY()+bullets.get(i).getHeight()<0 || bullets.get(i).getX()>getWidth() || bullets.get(i).getY()>getHeight()) && bullets.get(i).getVelocity()>0){
						bullets.get(i).setPos(Integer.MAX_VALUE, Integer.MAX_VALUE);
						bullets.get(i).setVelocity(0);
					}
					//bullet collision with asteroid
					for(int j=0; j<asteroids.size(); j++){
						if(bullets.get(i).intersects(asteroids.get(j).getX(), asteroids.get(j).getY(), asteroids.get(j).getWidth(), asteroids.get(j).getHeight())){
							bullets.get(i).setPos(Integer.MAX_VALUE, Integer.MAX_VALUE);
							bullets.get(i).setVelocity(0);
							asteroids.add(new Asteroid(asteroids.get(j).getX()+asteroids.get(j).getWidth()/4, asteroids.get(j).getY()+asteroids.get(j).getHeight()/4, asteroids.get(j).getWidth()/2, asteroids.get(j).getHeight()/2, bullets.get(i).getAngle()+toRadians(90), asteroids.get(j).getVelocity()*2/3));
							asteroids.add(new Asteroid(asteroids.get(j).getX()+asteroids.get(j).getWidth()/4, asteroids.get(j).getY()+asteroids.get(j).getHeight()/4, asteroids.get(j).getWidth()/2, asteroids.get(j).getHeight()/2, bullets.get(i).getAngle()+toRadians(-90), asteroids.get(j).getVelocity()*2/3));
							asteroids.remove(j);
						}
					}
				}

				//remove asteroids which are too small
				for(Iterator<Asteroid> i = asteroids.iterator(); i.hasNext(); ){
					Asteroid rock = i.next();
					if(rock.getWidth()<8 && rock.getHeight()<8){
						i.remove();
					}
				}
				
				paint(g);
			}
		};

		t = Executors.newSingleThreadScheduledExecutor();
		future = t.scheduleAtFixedRate(game, 100, 20, TimeUnit.MILLISECONDS);
	}

	public void keyPressed(KeyEvent e){

		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			angleRight = true;
			angleLeft = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			angleLeft = true;
			angleRight = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_UP){
			velUp = true;
			velDown = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			velDown = true;
			velUp = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			fireBullets = true;
		}
	}

	public void keyReleased(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			angleRight = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			angleLeft = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_UP){
			velUp = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			velDown = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			fireBullets = false;
		}
	}

	public void keyTyped(KeyEvent e){
		//not needed, but has to be here
	}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		//background
		graphicsBuffer.setColor(Color.black);
		graphicsBuffer.fillRect(0, 0, getWidth(), getHeight());
		graphicsBuffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphicsBuffer.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		
		//paint bullets
		graphicsBuffer.setColor(Color.red);
		for(int i=0; i<bullets.size(); i++){
			bullets.get(i).paint(graphicsBuffer);
		}

		//paint ship
		graphicsBuffer.setColor(Color.green);
		ship.paint(graphicsBuffer);

		//paint asteroids
		graphicsBuffer.setColor(Color.lightGray);
		for(Asteroid rock : asteroids){
			rock.paint(graphicsBuffer);
		}
		
		//show controls
		graphicsBuffer.setColor(Color.orange);
		graphicsBuffer.drawString(controls, getWidth()/2 - getStringWidth(controls)/2, 20);
		
		//show ship health
		if(shipHealth>=66){
			graphicsBuffer.setColor(Color.green);
		}else if(shipHealth>=33){
			graphicsBuffer.setColor(Color.yellow);
		}else if(shipHealth>0){
			graphicsBuffer.setColor(Color.red);
		}
		graphicsBuffer.drawString("Ship Health: " + shipHealth, 10, getHeight()-10);
		
		//show time taken
		double timeTaken = (System.currentTimeMillis() - startTime) / 1000.0;
		graphicsBuffer.setColor(Color.CYAN);
		String displayTime = "Time taken: ";
		displayTime += new DecimalFormat("##########0.0").format(timeTaken) + " seconds";
		graphicsBuffer.drawString(displayTime, getWidth() - getStringWidth(displayTime) - 10, getHeight() - 10);

		//if dead, display it
		if(shipHealth<=0){
			graphicsBuffer.setColor(Color.black);
			graphicsBuffer.fillRect(0, 0, getWidth(), getHeight());

			graphicsBuffer.setColor(Color.white);
			graphicsBuffer.setFont(new Font("Trebuchet MS", Font.BOLD, 40));
			graphicsBuffer.drawString("Oh dear, you are dead.", 100, getHeight()/2-20);
		}

		//if won, display it
		if(won){
			graphicsBuffer.setColor(Color.black);
			graphicsBuffer.fillRect(0, 0, getWidth(), getHeight());

			graphicsBuffer.setColor(Color.white);
			graphicsBuffer.setFont(new Font("Trebuchet MS", Font.BOLD, 40));
			graphicsBuffer.drawString("Congratulations, you won!", 80, getHeight()/2-20);
			graphicsBuffer.setFont(new Font("Trebuchet MS", Font.BOLD, 20));
			String winString = "You took a total of ";
			winString += new DecimalFormat("##########0.0").format(totalTimeInSeconds) + " seconds";
			winString += " to destroy all the asteroids.";
			graphicsBuffer.drawString(winString, 80, getHeight()/2+20);
		}

		//draw everything from buffer
		g2.drawImage(imageBuffer, 0,0, getWidth(), getHeight(), this);
	}
	
	private int getStringWidth(String string) {
		return graphicsBuffer.getFontMetrics().stringWidth(string);
	}
}