package brickBraker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.Timer;

import javax.swing.JPanel;


public class Gameplay extends JPanel implements KeyListener, ActionListener{
	private boolean play = false;
	private int score = 0;
	private int scorePerBrick;
	private int totalBricks = 21;
	
	private Timer timer;
	private int delay = 8;
	
	private int playerX ;
	
	
	private int ballposX ;
	private int ballposY ;
	
	private int[] easy = {30,1,1};
	private int[] medium = {20,2,2};
	private int[] hard = {10,3,5};
	
	//These values are level dependent
	private int level;
	private int impactBuffer;
	private int paddleStepIncrease;
	
	//Ball direction and speed variables
 	private int ballXdir ;
	private int ballYdir ;
	private int speedX ;
	private int speedY ;
	
	
	//paddle Step and Length control
	private int paddleStep = 10;
	private int paddleLength = 100;
	
	private int impacts = 0;
	
	private MapGenerator map;
	
	public Gameplay() {
		//Setting difficulty
		level = 2;
		setLevelValues();
		
		//Setting Game variables
		setIntitialGameVariables();
				
		//Drawing Bricks
		map = new MapGenerator(3, 7);
		
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		timer = new Timer(delay, this);
		timer.start();
	}
	
	public void paint(Graphics g) {
		//background
		g.setColor(Color.darkGray);
		g.fillRect(1, 1, Main.windowWidth-8, Main.windowHeight-8);
		
		//Drawing Map
		map.draw((Graphics2D)g);
		
		//borders
		g.setColor(Color.black);
		g.fillRect(0,0,3,592);
		g.fillRect(0,0,692,3);
		g.fillRect(691,0,3,592);
		
		//Scores
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString (""+score,590,30);
		
		//the paddle
		g.setColor(Color.white);
		g.fillRect(playerX, 550, paddleLength, 8);
		
		//Ball
		g.setColor(Color.red);
		g.fillOval(ballposX, ballposY, 20, 20);
		
		if(ballposY > 570) {
			play = false;
			ballXdir = 0;
			ballYdir = 0;
			g.setColor(Color.black);
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("Game Over, Scores: "+score, 190, 300);
			
			g.setFont(new Font("serif", Font.BOLD, 30));
			g.drawString("Press Enter to Restart ", 230, 350);
			
		}
		
		g.dispose();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();
		
		if(play) {
			
			Rectangle paddleRect = new Rectangle(playerX,550,paddleLength,8);
			Rectangle ballRect = new Rectangle(ballposX,ballposY,20,20);
			
			if(ballRect.intersects(paddleRect))	{
				bounceY();
				
				//When the impactBuffer is passed the speed, paddle length goes up depending on the level
				if(checkImpacts()) {
					changeSpeed();
					changePaddleLength();	
					changePaddleStep();
				}
			}
			
			A: for (int i = 0; i < map.map.length; i++) {
				for (int j = 0; j < map.map[0].length; j++) {
					if(map.map[i][j] > 0) {
						int brickX = j*map.brickWidth + 80;
						int brickY = i*map.brickHeight + 50;
						int brickWidth = map.brickWidth;
						int brickHeight = map.brickHeight;
						
						Rectangle bRect = new Rectangle(brickX, brickY, brickWidth,brickHeight);
						Rectangle brickRect = bRect;
						
						//Checking for impacts of ball and bricks
						if(ballRect.intersects(brickRect)) {
							map.setBrickValue(0, i, j);
							totalBricks--;
							score+=scorePerBrick;
							
							//bouncing off the ball from the intersected brick
							if(ballposX+19 <= brickRect.x || ballposX + 1 >=brickRect.x + brickRect.width) bounceX();
							else bounceY();
							
							//When one brick is hit you don't need to check for the rest of the bricks
							break A;
						}
					}
				}
			}
			moveBall();
			
			if(ballposX < 0 || ballposX > 670) bounceX();
			if(ballposY < 0) bounceY();
		}
		repaint();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() ==  KeyEvent.VK_RIGHT) {
			if(playerX>=600) playerX = 600;
			else moveRight();
		}
		if(e.getKeyCode() ==  KeyEvent.VK_LEFT) {
			if(playerX<10) playerX = 10;
			else moveLeft();
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(!play) {
				play=true;
			}
		}
		
	}

	private void setIntitialGameVariables() {
		Random random = new Random();
		
		ballposX = random.nextInt(300)+250;
		ballposY = 400;
		ballXdir = -1;
		ballYdir = -2;
		playerX = 310;
	}

	private boolean checkImpacts() {
		if(impacts >= impactBuffer) {
			impacts = 0; 
			return true;
		}
		return false;
	}
	private void changeSpeed() {
		speedX = ballXdir/Math.abs(ballXdir);
		speedY = ballYdir/Math.abs(ballYdir);
		ballXdir = ballXdir+speedX;
		ballYdir = ballYdir+speedY;
	}
	
	private void changePaddleStep() {
		paddleStep+=paddleStepIncrease;
	}
	
	private void changePaddleLength() {
		paddleLength+=10;
	}
	
	private void moveBall() {
		ballposX += ballXdir;
		ballposY += ballYdir;
	}
	
	private void setLevelValues() {
		if (level == 1) {
			impactBuffer = easy[0];
			scorePerBrick = easy[1];
			paddleStepIncrease = easy[2];
		}else if (level == 2) {
			impactBuffer = medium[0];
			scorePerBrick = medium[1];
			paddleStepIncrease = medium[2];
		}else {
			impactBuffer = hard[0];
			scorePerBrick = hard[1];
			paddleStepIncrease = hard[2];
		}
	}
	
	private void moveRight() {
		play = true;
		playerX+=paddleStep;
	}
	
	private void moveLeft() {
		play = true;
		playerX-=paddleStep;
	}
	
	private void bounceX() {
		ballXdir = -ballXdir;
		impacts++;
	}
	
	private void bounceY() {
		ballYdir = -ballYdir;
		impacts++;
	}
}
