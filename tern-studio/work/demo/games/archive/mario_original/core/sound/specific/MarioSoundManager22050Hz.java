package devforrest.mario.core.sound.specific;
import javax.sound.sampled.AudioFormat;

import devforrest.mario.core.sound.Sound;
import devforrest.mario.core.sound.SoundManager;


public class MarioSoundManager22050Hz extends SoundManager {
	
	private Sound bump, kick, coin, jump, pause, itemSprout, bonusPoints, healthUp, healthDown;

	public MarioSoundManager22050Hz(AudioFormat format) {
		super(format);
 		bump = getSound("sounds/bump.wav");
 		kick = getSound("sounds/kick.wav");
 		coin = getSound("sounds/coin.wav");
 		jump = getSound("sounds/jump.wav");
 		pause = getSound("sounds/pause.wav");
 		itemSprout = getSound("sounds/item_sprout.wav");
 		bonusPoints = getSound("sounds/veggie_throw.wav");
 		healthUp = getSound("sounds/power_up.wav");
 		healthDown = getSound("sounds/power_down.wav");
	}
	
	public void playHealthUp() {
		play(healthUp);
	}
	
	public void playHealthDown() {
		play(healthDown);
	}
	
	public void playBonusPoints() {
		play(bonusPoints);
	}
	
	public void playItemSprout() {
		play(itemSprout);
	}
	
	public void playCoin() {
		play(coin);
	}
	
	public void playKick() {
		play(kick);
	}
	
	public void playBump() {
		play(bump);
	}
	
	public void playJump() {
		play(jump);
	}
	
	public void playPause() {
		play(pause);
	}
}
