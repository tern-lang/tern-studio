package mario.objects.creatures;



import mario.core.MarioResourceManager;
import mario.core.animation.Animation;
import mario.core.tile.TileMap;
import mario.objects.base.Creature;
import android.graphics.Bitmap;





public class Score extends Creature {
	
	public Animation oneHundred;
	private static Bitmap one_hundred;
	private static boolean initialized=false;
	public Score(int x, int y) {
		super(x, y);
		setIsItem(true);
		
		dy = -.45f;
		if (!initialized){
			one_hundred = MarioResourceManager.Score_100_New6;
			initialized=true;
		}
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		oneHundred = new DeadAfterAnimation();
		
		oneHundred.addFrame(one_hundred, 380);
		oneHundred.addFrame(one_hundred, 380);	
		setAnimation(oneHundred);
	}
	
	public void updateCreature(TileMap map, int time) {
		this.update((int) time);
		y = y + dy * time;
		if(dy < 0) {
			dy = dy + .032f;
		} else {
			dy = 0;
		}
	}

}
