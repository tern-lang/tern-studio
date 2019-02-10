package devforrest.mario.core;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import devforrest.mario.core.sound.specific.MarioSoundManager22050Hz;
import devforrest.mario.core.tile.GameTile;
import devforrest.mario.core.tile.TileMap;
import devforrest.mario.objects.creatures.Coin;
import devforrest.mario.objects.creatures.Goomba;
import devforrest.mario.objects.creatures.Platform;
import devforrest.mario.objects.creatures.RedKoopa;
import devforrest.mario.objects.creatures.RedShell;
import devforrest.mario.objects.tiles.QuestionBlock;
import devforrest.mario.objects.tiles.RotatingBlock;
import devforrest.mario.objects.tiles.SlopedTile;
import devforrest.mario.util.SpriteMap;



public class GameLoader {
	
	private ArrayList<BufferedImage> plain;
	private BufferedImage[] plainTiles;
	
	private BufferedImage sloped_image;
	private BufferedImage grass_edge;
	private BufferedImage grass_center;
	
	public GameLoader() {
		 
		plain = new ArrayList<BufferedImage>();
		plainTiles = (new SpriteMap("tiles/Plain_Tiles.png", 6, 17)).getSprites();
		
		for (BufferedImage bImage : plainTiles) {
			plain.add(bImage);
		}
		
		sloped_image = loadImage("items/Sloped_Tile.png");
		grass_edge = loadImage("items/Grass_Edge.png");
		grass_center = loadImage("items/Grass_Center.png");
	}
	
	public BufferedImage loadImage(String filename) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(filename));
		} catch (IOException e) { }
		return img;
	}
	
	// BufferedImage -> Image
	public static Image toImage(BufferedImage bufferedImage) {
	    return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
	}

	// loads a tile map, given a map to load..
    // use this to load the background and foreground. Note: the status of the tiles (ie collide etc)
    // is irrelevant. Why? I don't check collision on maps other than the main map. 
    public TileMap loadOtherMaps(String filename) throws IOException {
		// lines is a list of strings, each element is a row of the map
		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;
		int height = 0;
		
		// read in each line of the map into lines
		Scanner reader = new Scanner(new File(filename));
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			if(!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		height = lines.size(); // number of elements in lines is the height
		
		TileMap newMap = new TileMap(width, height);
		for (int y=0; y < height; y++) {
			String line = lines.get(y);
			for (int x=0; x < line.length(); x++) {
				char ch = line.charAt(x);
				
				if (ch == 'n') {
					newMap.setTile(x, y, plain.get(92));
				} else if (ch == 'm') {
					newMap.setTile(x, y, plain.get(93));
				} else if (ch == 'v') {
					newMap.setTile(x, y, plain.get(90));
				} else if (ch == 'b') {
					newMap.setTile(x, y, plain.get(91));
				} else if (ch == 'q') { // rock left
					newMap.setTile(x, y, plain.get(48));
				} else if (ch == 'w') { // rock right
					newMap.setTile(x, y, plain.get(49));
				} 
			}
		}
		return newMap;	
	}
    	
    // Use this to load the main map
	public TileMap loadMap(String filename, MarioSoundManager22050Hz soundManager) throws IOException {
		// lines is a list of strings, each element is a row of the map
		ArrayList<String> lines = new ArrayList<String>();
		int width = 0;
		int height = 0;
		
		// read in each line of the map into lines
		Scanner reader = new Scanner(new File(filename));
		while(reader.hasNextLine()) {
			String line = reader.nextLine();
			if(!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		height = lines.size(); // number of elements in lines is the height
		
		TileMap newMap = new TileMap(width, height);
		for (int y=0; y < height; y++) {
			String line = lines.get(y);
			for (int x=0; x < line.length(); x++) {
				char ch = line.charAt(x);
				
				int pixelX = GameRenderer.tilesToPixels(x);
				int pixelY = GameRenderer.tilesToPixels(y);
				// enumerate the possible tiles...
				if (ch == 'G') {
					newMap.creatures().add(new Goomba(pixelX, pixelY, soundManager));
				} else if (ch == 'K') {
					newMap.creatures().add(new RedKoopa(pixelX, pixelY, soundManager));
				} else if (ch == 'V') {
					GameTile t = new GameTile(pixelX, pixelY, plain.get(56));
					newMap.setTile(x, y, t);
				} else if (ch == 'R') {
					RotatingBlock r = new RotatingBlock(pixelX, pixelY);
					newMap.setTile(x, y, r);
					newMap.animatedTiles().add(r);
				} else if (ch == '3') {
					GameTile t = new GameTile(pixelX, pixelY, plain.get(4));
					newMap.setTile(x, y, t);
				} else if (ch == '4') {
					GameTile t = new GameTile(pixelX, pixelY, plain.get(10));
					newMap.setTile(x, y, t);
				} else if (ch == '2') {
					GameTile t = new GameTile(pixelX, pixelY, plain.get(86));
					newMap.setTile(x, y, t);
				} else if (ch == 'Q') {
					QuestionBlock q = new QuestionBlock(pixelX, pixelY, newMap, soundManager, true, false);
					newMap.setTile(x, y, q);
					newMap.animatedTiles().add(q);
				} else if (ch == 'W') {
					QuestionBlock q = new QuestionBlock(pixelX, pixelY, newMap, soundManager, false, true);
					newMap.setTile(x, y, q);
					newMap.animatedTiles().add(q);
				} else if (ch == 'S') {
					newMap.creatures().add(new RedShell(pixelX, pixelY, newMap, soundManager, true));
				} else if(ch == 'C') {
					newMap.creatures().add(new Coin(pixelX, pixelY));
				} else if(ch == 'P') {
					Platform p = new Platform(pixelX, pixelY);
					newMap.creatures().add(p);
				} else if(ch == '9') {
					SlopedTile t = new SlopedTile(pixelX, pixelY, sloped_image, true);
					newMap.setTile(x, y, t);
					newMap.slopedTiles().add(t);
				} else if(ch == '8') {
					GameTile t = new GameTile(pixelX, pixelY, grass_edge);
					newMap.setTile(x, y, t);
				} else if(ch == '7') {
					GameTile t = new GameTile(pixelX, pixelY, grass_center);
					newMap.setTile(x, y, t);
				}
			}
		}
		return newMap;	
	}

}
