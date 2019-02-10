package mario.objects.creatures;



import mario.core.MarioResourceManager;
import mario.core.animation.Animation;
import mario.core.tile.GameTile;
import android.graphics.Bitmap;

public class Tree extends GameTile {
	
	private static Bitmap[] c = {MarioResourceManager.Tree_1, MarioResourceManager.Tree_2};
	public static Animation swing = new Animation(1200).addFrame(c[0]).addFrame(c[1]);

	public Tree(int pixelX, int pixelY) {
		super(pixelX, pixelY,null,null);
		setIsCollidable(false);
		setAnimation(swing);
	}
	
}
