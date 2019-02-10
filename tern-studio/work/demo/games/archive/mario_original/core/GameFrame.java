package devforrest.mario.core;

import javax.swing.JFrame;

// This is the main entry point..

public class GameFrame extends JFrame {
   
   private static final int RESOLUTION_WIDTH = 420;
   private static final int RESOLUTION_HEIGHT = 330;
   private static final int RESOLUTION_SCALE = 1;
   private static final int FRAME_WIDTH = RESOLUTION_WIDTH * RESOLUTION_SCALE;
   private static final int FRAME_HEIGHT = RESOLUTION_HEIGHT * RESOLUTION_SCALE;
   
	public GameFrame() {	      
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		setTitle("Game Frame"); 
		GamePanel panel = new GamePanel(RESOLUTION_WIDTH, RESOLUTION_HEIGHT, RESOLUTION_SCALE);
		add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);	
	}
	
	public static void main(String[] args) {
		new GameFrame();
	}

}
