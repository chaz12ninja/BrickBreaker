package brickBraker;

import javax.swing.JFrame;


public class Main {
	
	public static int windowWidth = 700;
	public static int windowHeight = 600;
	
	public static void main(String[] args) {
		JFrame obj = new JFrame();
		
		Gameplay gamePlay = new Gameplay();
		obj.setBounds( 10, 10, windowWidth, windowHeight);
		obj.setTitle("Breakout Ball");
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.add(gamePlay);
	}
	
}
