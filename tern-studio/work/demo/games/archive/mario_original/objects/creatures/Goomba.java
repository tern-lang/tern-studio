package devforrest.mario.objects.creatures;



import java.awt.image.BufferedImage;
import java.util.Random;

import devforrest.mario.core.animation.Animation;
import devforrest.mario.core.sound.specific.MarioSoundManager22050Hz;
import devforrest.mario.objects.base.Creature;
import devforrest.mario.util.ImageManipulator;





public class Goomba extends Creature {
	
	private Animation waddle, dead, flip;
	
	public Goomba(int x, int y, MarioSoundManager22050Hz soundManager) {
		
		super(x, y, soundManager);
		
		BufferedImage w1 = ImageManipulator.loadImage("baddies/Goomba_Normal_1.png");
		BufferedImage w2 = ImageManipulator.loadImage("baddies/Goomba_Normal_2.png");
		BufferedImage smashed = ImageManipulator.loadImage("baddies/Goomba_Dead.png");
		BufferedImage flipped = ImageManipulator.loadImage("baddies/Goomba_Flip.png");
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}

		waddle = new Animation(150).addFrame(w1).addFrame(w2);
		dead = new DeadAfterAnimation().setDAL(100).addFrame(smashed).setDAL(20).addFrame(smashed);
		flip = new Animation().addFrame(flipped).addFrame(flipped);
		setAnimation(waddle);
	}
	
	public void wakeUp() {
		Random r = new Random();
		super.wakeUp();
		dx = (r.nextInt(3) == 0) ? -.03f : .03f;
	}
	
	public void jumpedOn() {
		setAnimation(dead);
		setIsCollidable(false);
		dx = 0;
	}
	
	public void flip() {
		setAnimation(flip);
		setIsFlipped(true);
		setIsCollidable(false);
		dy = -.2f;
		dx = 0;
	}
}
