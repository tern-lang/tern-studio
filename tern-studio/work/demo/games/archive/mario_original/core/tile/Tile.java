package devforrest.mario.core.tile;


import java.awt.Graphics;
import java.awt.image.BufferedImage;

import devforrest.mario.core.GameRenderer;
import devforrest.mario.core.animation.Animatible;
import devforrest.mario.core.animation.Animation;


public class Tile extends Animatible {
	
	// fields
	private int tileX;
	private int tileY;
	private int pixelX;
	private int pixelY;
	protected BufferedImage img;
	
	public Tile(int pixelX, int pixelY, Animation anim, BufferedImage img) {
		tileX = GameRenderer.pixelsToTiles(pixelX);
		tileY = GameRenderer.pixelsToTiles(pixelY);
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.img = img;
		setAnimation(anim);
	}
	
	public Tile(int pixelX, int pixelY, BufferedImage img) {
		this(pixelX, pixelY, null, img);
	}
	
	public void draw(Graphics g, int pixelX, int pixelY) {
		g.drawImage(getImage(), pixelX, pixelY, null);
	}
	
	public void draw(Graphics g, int pixelX, int pixelY, int offsetX, int offsetY) {
		draw(g, pixelX + offsetX, pixelY + offsetY);
	}
	
	public BufferedImage getImage() {
		return (currentAnimation() == null) ? img : currentAnimation().getImage();
	}
	
	public int getPixelX() {
		return pixelX;
	}
	
	public int getPixelY() {
		return pixelY;
	}
	
	public int getWidth() {
		return getImage().getWidth(null);
	}
	
	public int getHeight() {
		return getImage().getHeight(null);
	}
} // Tile
