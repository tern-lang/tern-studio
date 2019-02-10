package mario.objects.creatures;



import mario.core.MarioResourceManager;
import mario.core.animation.Animation;
import mario.core.tile.TileMap;
import mario.objects.base.Creature;
import android.graphics.Bitmap;





public class Coin extends Creature {
	
	private static Bitmap[] c = { MarioResourceManager.Coin_5,MarioResourceManager.Coin_6,
		MarioResourceManager.Coin_7, MarioResourceManager.Coin_8};
	public static Animation turn = new Animation(150).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
	// Alternate Animation;
	//private static Bitmap[] c = { MarioResourceManager.Coin_1,MarioResourceManager.Coin_2,
	//	MarioResourceManager.Coin_3, MarioResourceManager.Coin_4};
	
	//private Animation turn;
	private Animation shoot;
	
	public Coin(int pixelX, int pixelY) {
		
		super(pixelX, pixelY);
		setIsItem(true);
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		//turn = new Animation(1000).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
		shoot = new DeadAfterAnimation().setDAL(120).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
		setAnimation(turn);
	}
	
//	public void draw(Graphics g, int pixelX, int pixelY) {
//		float[] scales = { .6f, .6f, .6f, 8f};
//		float[] offsets = new float[4];
//		RescaleOp rop = new RescaleOp(scales, offsets, null);
//		
//		//RescaleOp rop = new RescaleOp(1.0f, 1f, null);
//		
//		BufferedImage source= currentAnimation().getImage();
//		BufferedImage adjusted=new BufferedImage(source.getWidth(),source.getHeight(),BufferedImage.TYPE_INT_BGR);
//		Graphics2D ug=adjusted.createGraphics();
//		ug.drawImage(source,0,0,null);
//		ug.dispose();
//		BufferedImageOp filter=new RescaleOp(1f, 100f,null);
//
//		Graphics2D g2d = (Graphics2D) g;
//		g2d.drawImage(adjusted, filter, pixelX, pixelY);
//	}
//	
//	public void draw(Graphics g, int pixelX, int pixelY, int offsetX, int offsetY) {
//		draw(g, pixelX + offsetX, pixelY + offsetY);	
//	}
	
	public void updateCreature(TileMap map, int time) {
		if(currentAnimation() == shoot) {
			super.update(time);
			y = y + dy * time;
			if(dy < 0) {
				dy = dy + .018f;
			} 
		}
	}
	
	public void shoot() {
		setIsCollidable(false);
		setAnimation(shoot);
		dy = -.3f;
	}

}
