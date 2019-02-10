package devforrest.mario.objects.tiles;

import java.awt.image.BufferedImage;
import java.util.Random;

import devforrest.mario.core.animation.Animation;
import devforrest.mario.core.sound.specific.MarioSoundManager22050Hz;
import devforrest.mario.core.tile.GameTile;
import devforrest.mario.core.tile.TileMap;
import devforrest.mario.objects.creatures.Coin;
import devforrest.mario.objects.creatures.Mushroom;
import devforrest.mario.objects.creatures.Score;
import devforrest.mario.util.ImageManipulator;

public class QuestionBlock extends GameTile {

	private MarioSoundManager22050Hz soundManager;
	private TileMap map;
	
	private Animation active;
	private Animation dead;
	private boolean isActive;
	private boolean hasCoin;
	private boolean hasMushroom;
	
	public QuestionBlock(int pixelX, int pixelY, TileMap map, MarioSoundManager22050Hz soundManager, boolean hasCoin,
			boolean hasMushroom) {
		
		// int pixelX, int pixelY, Animation anim, Image img
		super(pixelX, pixelY, null, null);
		
		setIsSloped(false);
		isActive = true;
		this.hasCoin = hasCoin;
		this.hasMushroom = hasMushroom;
		this.soundManager = soundManager;
		this.map = map;

		BufferedImage q[] = { ImageManipulator.loadImage("items/Question_Block_0.png"), ImageManipulator.loadImage("items/Question_Block_1.png"),
				ImageManipulator.loadImage("items/Question_Block_2.png"), ImageManipulator.loadImage("items/Question_Block_3.png"),
				ImageManipulator.loadImage("items/Question_Block_Dead.png") };
		
		Random r = new Random();
		active = new Animation(r.nextInt(20) + 140).addFrame(q[0]).addFrame(q[1]).addFrame(q[2]).addFrame(q[3]);
		dead = new Animation(2000).addFrame(q[4]);
		setAnimation(active);
	}
	
	public void update(int time) {
		super.update(time);
		if(getOffsetY() != 0) { setOffsetY(getOffsetY() + 2); }
	}
	
	public void doAction() {
		if(isActive) {
			if(hasCoin) {
				setOffsetY(-10);
				soundManager.playCoin();
				Coin newCoin = new Coin(getPixelX(), getPixelY());
				Score score = new Score(getPixelX(), getPixelY());
				map.creaturesToAdd().add(newCoin);
				map.creaturesToAdd().add(score);
				newCoin.shoot();
			} else if(hasMushroom) {
				setOffsetY(-10);
				soundManager.playItemSprout();
				Mushroom shroom = new Mushroom(getPixelX(), getPixelY()-26);
				map.creaturesToAdd().add(shroom);
			}
			setAnimation(dead);
			isActive = false;
		}
	}
}