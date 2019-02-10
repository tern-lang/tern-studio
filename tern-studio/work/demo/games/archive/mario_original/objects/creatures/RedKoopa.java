package devforrest.mario.objects.creatures;



import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

import devforrest.mario.core.animation.Animation;
import devforrest.mario.core.sound.specific.MarioSoundManager22050Hz;
import devforrest.mario.objects.base.Creature;
import devforrest.mario.util.ImageManipulator;





public class RedKoopa extends Creature {
	
	private Animation left;
	private Animation right;
	private Animation dead;
	private Animation flip;
	private Random r;
	
	public RedKoopa(int x, int y, MarioSoundManager22050Hz soundManager) {
		
		super(x, y, soundManager);
		r = new Random();
		
		BufferedImage left_1 = ImageManipulator.loadImage("baddies/Koopa_Red_Left_1.png");
		BufferedImage left_2 = ImageManipulator.loadImage("baddies/Koopa_Red_Left_2.png");
		BufferedImage right_1 = ImageManipulator.loadImage("baddies/Koopa_Red_Right_1.png");
		BufferedImage right_2 = ImageManipulator.loadImage("baddies/Koopa_Red_Right_2.png");
		BufferedImage shell = ImageManipulator.loadImage("baddies/Red_Shell_1.png");
		BufferedImage flipped = ImageManipulator.loadImage("baddies/Red_Shell_Flip.png");
		
		left = new Animation(150).addFrame(left_1).addFrame(left_2);
		right = new Animation(150).addFrame(right_1).addFrame(right_2);
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		dead = new DeadAfterAnimation();
		flip = new DeadAfterAnimation();
		dead.addFrame(shell, 10);
		dead.addFrame(shell, 10);
		flip.addFrame(flipped, 1200);
		flip.addFrame(flipped, 1200);
		setAnimation(left);
	}
	
	public void xCollide(Point p) {
		super.xCollide(p);
		if(currentAnimation() == left) {
			setAnimation(right);
		} else {
			setAnimation(left);
		}
	}
	
	public void creatureXCollide() {
		if(dx > 0) {
			x = x - 2;
			setAnimation(left);
		} else {
			setAnimation(right);
			x = x + 2;
		}
		dx = -dx;
	}
	
	public void flip() {
		setAnimation(flip);
		setIsFlipped(true);
		setIsCollidable(false);
		dy = -.2f;
		dx = 0;
	}
	
	public void wakeUp() {
		super.wakeUp();
		int rNum = r.nextInt(3);
			if(rNum == 0 || rNum == 1) {
				dx = -.03f;
				setAnimation(left);
			} else {
				dx = .03f;
				setAnimation(right);
			}
	}
	
	public void jumpedOn() {
		setAnimation(dead);
		setIsCollidable(false);
		dx = 0;
	}
}
