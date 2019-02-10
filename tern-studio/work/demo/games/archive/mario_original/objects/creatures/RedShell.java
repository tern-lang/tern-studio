package devforrest.mario.objects.creatures;


import java.awt.Point;
import java.awt.image.BufferedImage;

import devforrest.mario.core.animation.Animation;
import devforrest.mario.core.sound.specific.MarioSoundManager22050Hz;
import devforrest.mario.core.tile.GameTile;
import devforrest.mario.core.tile.TileMap;
import devforrest.mario.objects.base.Creature;
import devforrest.mario.util.ImageManipulator;





public class RedShell extends Creature {
	
	private Animation still;
	private Animation rotate;
	private Animation flip;
	
	private TileMap map;
	private boolean isMoving;
	
	public RedShell(int x, int y, TileMap map, MarioSoundManager22050Hz soundManager, boolean isStill) {
		
		super(x, y, soundManager);
		this.map = map;
		setIsAlwaysRelevant(true);
 		
		BufferedImage stay = ImageManipulator.loadImage("baddies/Red_Shell_1.png");
		BufferedImage rotate_1 = ImageManipulator.loadImage("baddies/Red_Shell_2.png");
		BufferedImage rotate_2 = ImageManipulator.loadImage("baddies/Red_Shell_3.png");
		BufferedImage rotate_3 = ImageManipulator.loadImage("baddies/Red_Shell_4.png");
		BufferedImage flipped = ImageManipulator.loadImage("baddies/Red_Shell_Flip.png");
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		still = new Animation();
		rotate = new Animation();
		flip = new DeadAfterAnimation();
		
		still.addFrame(stay, 150);
		rotate.addFrame(rotate_1, 30);
		rotate.addFrame(stay, 30);
		rotate.addFrame(rotate_2, 30);
		rotate.addFrame(rotate_3, 30);
		rotate.addFrame(rotate_1, 30);
		flip.addFrame(flipped, 1200);
		flip.addFrame(flipped, 1200);
		
		wakeUp();
		isMoving = false;
		setAnimation(still);
		dx = 0;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public void xCollide(Point p) {
		super.xCollide(p);
		GameTile tile = map.getTile(p.x, p.y);
		if(this.isOnScreen()) {
			soundManager.playBump();
			if(tile != null) {
				tile.doAction();
			}
		}
	}
	
	public void flip() {
		setAnimation(flip);
		setIsFlipped(true);
		setIsCollidable(false);
		dy = -.2f;
		dx = 0;
	}
	
	// if you run or jump on the shell faster, the shell moves faster.
	public void jumpedOn(boolean fromRight, float attackerSpeed) {
		if(isMoving) {
			isMoving = false;
			setAnimation(still);
			dx = 0;
		} else {
			isMoving = true;
			setAnimation(rotate);
			if(fromRight) {
				if(attackerSpeed > .2f) {
					dx = .24f;
				} else if(attackerSpeed > .16) { 
					dx = .23f;
				} else {
					dx = .16f;
				}
			} else {
				if(attackerSpeed < -.2f) {
					dx = -.24f;
				} else if(attackerSpeed < -.16) {
					dx = -.23f;
				} else {
					dx = -.16f;
				}
			}
		}
	}
}
