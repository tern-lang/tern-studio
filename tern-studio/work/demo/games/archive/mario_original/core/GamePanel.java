package devforrest.mario.core;
/**
 * GamePanel extends Jpanel. Contains the main game loop.
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import devforrest.mario.core.animation.SpriteListener;
import devforrest.mario.core.sound.MidiPlayer;
import devforrest.mario.core.sound.specific.MarioSoundManager10512Hz;
import devforrest.mario.core.sound.specific.MarioSoundManager22050Hz;
import devforrest.mario.core.tile.GameTile;
import devforrest.mario.core.tile.TileMap;
import devforrest.mario.objects.base.Creature;
import devforrest.mario.objects.creatures.Coin;
import devforrest.mario.objects.mario.Mario;
import devforrest.mario.util.ImageManipulator;

public class GamePanel extends JPanel implements Runnable {

	private int panelWidth;
	private int panelHeight;
	private int panelScale;
	private Graphics dbg;
	private Image dbImage = null;

	private boolean running = false; 
	private boolean gameOver = false;
	private boolean gameFreeze = false;
	
	private Thread animator;
	private int period = 20; 
	
	private Mario mario;
	private TileMap map;
	private TileMap backgroundMap;
	private TileMap foregroundMap;
	private GameRenderer renderer;
	private GameLoader manager;
	
	
	private MidiPlayer player;
	private MarioSoundManager22050Hz SM_22050_Hz;
	private MarioSoundManager10512Hz SM_10512_Hz;
	
	public GamePanel(int w, int h, int scale) {
		
		this.panelWidth = w;
		this.panelHeight = h;
		this.panelScale = scale;
		
		SM_22050_Hz = new MarioSoundManager22050Hz(new AudioFormat(22050, 8, 1, true, true));
		SM_10512_Hz = new MarioSoundManager10512Hz(new AudioFormat(10512, 8, 1, true, true));
 		mario = new Mario(SM_22050_Hz);
		
		try {
			manager = new GameLoader();
			renderer = new GameRenderer();
			renderer.setBackground(ImageIO.read(new File("backgrounds/background2.png")));
			map = manager.loadMap("maps/map2.txt", SM_22050_Hz); // use the ResourceManager to load the game map
			//backgroundMap = manager.loadOtherMaps("backgroundMap.txt");
			//foregroundMap = manager.loadOtherMaps("foregroundMap.txt");
			map.setPlayer(mario); // set the games main player to mario
		} catch (IOException e){
			System.out.println("Invalid Map.");
		}
		
		player = new MidiPlayer();
		Sequence sequence;
		Random r = new Random();
		int rNum = r.nextInt(4);
		if(rNum == 0) {
			sequence = player.getSequence("sounds/smwovr2.mid");
	        player.play(sequence, true);
		} else if(rNum == 1) {
			sequence = player.getSequence("sounds/smwovr2.mid");
	        player.play(sequence, true);
		} else if(rNum == 2) {
			sequence = player.getSequence("music/smb_hammerbros.mid");
	        player.play(sequence, true);
		} else if(rNum == 3) {
			sequence = player.getSequence("music/smrpg_nimbus1.mid");
	        player.play(sequence, true);
		}
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		this.addKeyListener(new SpriteListener(mario));
		this.addKeyListener(new GameListener());
		this.setFocusable(true); 
	}
	
	/**
	 * Automatically called as GamePanel is being added to its enclosing GUI component,
	 * and so is a good place to initiate the animation thread.
	 */
	public void addNotify() {
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}
	
	/**
	 * Start the game thread.
	 */
	private void startGame() {
		if(animator == null || !running) {
			animator = new Thread(this, "The Animator V 3.0");
			animator.start();
		}
	}
	
	/**
	 * Stop the game.
	 */
	public void stopGame() { running = false; }
	
	/**
	 * Defines a single game loop.
	 */
	public void gameAction() {
		gameUpdate(); // Update game state.
		gameRender(); // Draw to the double buffer.
		paintScreen(); // Draw double buffer to screen.
	}
	
	/**
	 * The main game loop - repeatedly update, repaint, sleep.
	 */
	public void run() {
		
		running = true;
		while(running) {
			if(!gameFreeze) {
				gameAction();
			}
			
			try {
				Thread.sleep(period);
			} catch(InterruptedException ex){}
		}
		System.exit(0); // so enclosing JFrame/JApplet exits
	}
	
	/**
	 * Update the state of all game objects. In the future this game logic
	 * should probably be abstracted out of this class.
	 */
	private void gameUpdate() {
		
		if (!gameOver) {
			// Update all relevant Creatures.
			for(int i = 0; i < map.relevantCreatures().size(); i++) {
				Creature c = map.relevantCreatures().get(i);
				if(!(c instanceof Coin)) {
					c.updateCreature(map, period);
					mario.playerCollision(map, c);
					for(Creature other : map.relevantCreatures()) {
						c.creatureCollision(other);
					}
				} else {
					c.updateCreature(map, period);
					mario.playerCollision(map, c);
				}
			}
			
			// Debugging information:
			//System.out.println("relevant creatures size: " + map.relevantCreatures().size());
			//System.out.println("creatures size: " + map.creatures().size());
			//System.out.println(map.platforms().size());
			
			for(GameTile tile : map.animatedTiles()) {
	            tile.collidingCreatures().clear();  // clear the colliding sprites on the tile
	            tile.update(20);
			}
        
			// Add creatures that need to be created. They are added here to avoid concurrent modifcation errors.
            for(Creature c : map.creaturesToAdd()) {
            	map.creatures().add(c);
            }
            
            map.creaturesToAdd().clear(); // This line MUST be called BEFORE mario.update(). Why?
            							  // If it is called after, all the creatures that are created
            							  // as a result of mario colliding are not added next update because
            							  // they are cleared immediately afterwards.

			mario.update(map, period);
			Coin.turn.update(period);
			map.relevantCreatures().clear();
			map.platforms().clear();
		}
	}
	
	/**
	 * Draws the game image to the buffer.
	 */
	private void gameRender() {
		if(dbImage == null) {
			dbImage = createImage(this.panelWidth, this.panelHeight);
			return;
		}
	    dbg = dbImage.getGraphics();    
		renderer.draw((Graphics2D) dbg, map, backgroundMap, foregroundMap, panelWidth, panelHeight);
	}
	
	/**
	 * Draws the game image to the screen by drawing the buffer.
	 */
	private void paintScreen() {	
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))  {
			   BufferedImage scale = ImageManipulator.scaleImage((BufferedImage)dbImage, panelScale);
				g.drawImage(scale, 0, 0, null);
				g.dispose();
			} 
		} catch (Exception e) { System.out.println("Graphics context error: " + e); }
	}
	
	/**
	 * Adds debugging features so it is possible to single step a game loop one by one.
	 * 'Z' pauses the game.
	 * 'X' resumes the game.
	 * '1' runs a single game loop if the game if paused.
	 * 'L' runs a single game loop if pressed and continously runs the game loop if held.
	 */
	class GameListener extends KeyAdapter {
		
	    public void keyReleased(KeyEvent e) {
	    	int key = e.getKeyCode();
			
	    	// 'Z' is pressed.
	        if (key == KeyEvent.VK_Z) { // pause
	        	if(GamePanel.this.gameFreeze == false) {
		        	GamePanel.this.gameFreeze = true;
		        	GamePanel.this.player.setPaused(true);
		        	GamePanel.this.SM_22050_Hz.playPause();
	        	}
	        }
	        
	        // 'X' is pressed.
	        if (key == KeyEvent.VK_X) { // resume
	        	if(GamePanel.this.gameFreeze == true) {
		        	GamePanel.this.gameFreeze = false;
		        	GamePanel.this.player.setPaused(false);
		        	GamePanel.this.SM_22050_Hz.playPause();
	        	}
	        }
	        
	        // '1' is pressed.
	        if (key == KeyEvent.VK_1) {
	        	if(GamePanel.this.gameFreeze == true) {
	        		System.out.println();
	        		System.out.println("Game Update (1) Starting...");
	        		GamePanel.this.gameAction();
	        		System.out.println();
	        		System.out.println("Game Update (1) Completed.");
	        	}
	        }

	    } 
	    
	    // 'L' is pressed or held.
	    public void keyPressed(KeyEvent e) {
	    	int key = e.getKeyCode();
	    	if (key == KeyEvent.VK_L) {
	    		GamePanel.this.gameAction();
	    	}

	    }
		
	}
}
