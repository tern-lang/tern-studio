package devforrest.mario.core.sound.specific;
import java.util.Random;

import javax.sound.sampled.AudioFormat;

import devforrest.mario.core.sound.Sound;
import devforrest.mario.core.sound.SoundManager;


public class MarioSoundManager10512Hz extends SoundManager {
	
	private Sound hurt1, hurt2, yahoo1, yahoo2;

	public MarioSoundManager10512Hz(AudioFormat format) {
		super(format);
 		hurt1 = getSound("sounds/mario_ooh.wav");
 		hurt2 = getSound("sounds/mario_oh.wav");
 		yahoo1 = getSound("sounds/mario_waha.wav");
 		yahoo2 = getSound("sounds/mario_woohoo.wav");
	}
	
	public void playHurt() {
		Random r = new Random();
		int rNum = r.nextInt(2);
		if(rNum == 0) {
			play(hurt1);
		} else {
			play(hurt2);
		}
	}
	
	public void playCelebrate() {
		Random r = new Random();
		int rNum = r.nextInt(2);
		if(rNum == 0) {
			play(yahoo1);
		} else {
			play(yahoo2);
		}
	}
}