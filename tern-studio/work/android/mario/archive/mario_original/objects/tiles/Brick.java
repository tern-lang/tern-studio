package mario.objects.tiles;

import mario.core.MarioSoundManager;
import mario.core.tile.GameTile;
import mario.core.tile.TileMap;
import mario.objects.creatures.Coin;
import mario.objects.creatures.Mushroom;
import mario.objects.creatures.Score;
import mario.particles.ParticleSystem;
import android.graphics.Bitmap;



public class Brick extends GameTile {

	private MarioSoundManager soundManager;
	private TileMap map;
	
	private int numCoins;
	private boolean hasMushroom;
	
	public Brick(int pixelX, int pixelY, TileMap map, Bitmap img,MarioSoundManager soundManager,int numCoins,
			boolean hasMushroom) {
		// int pixelX, int pixelY, Animation anim, Image img
		super(pixelX, pixelY, null,img);
	
		setIsSloped(false);
		this.numCoins = numCoins;
		this.hasMushroom = hasMushroom;
		this.soundManager = soundManager;
		this.map = map;
	}
	
	@Override
	public void update(int time) {
		//super.update(time);
		if(getOffsetY() != 0) { setOffsetY(getOffsetY() + 2); }
	}
	
	@Override
	public void doAction() {

		if (numCoins > 0) {
			numCoins--;
			setOffsetY(-10);
			soundManager.playCoin();
			Coin newCoin = new Coin(getPixelX(), getPixelY());
			Score score = new Score(getPixelX(), getPixelY());
			map.creaturesToAdd().add(newCoin);
			map.creaturesToAdd().add(score);
			newCoin.shoot();
		} else if (hasMushroom) {
			setOffsetY(-10);
			soundManager.playItemSprout();
			Mushroom shroom = new Mushroom(getPixelX(), getPixelY() - 26);
			map.creaturesToAdd().add(shroom);
		} else {// (((Mario)map.getPlayer()).){
			soundManager.playBrickShatter();
			map.particleSystem = new ParticleSystem(getPixelX(), getPixelY(), 8);
			map.getTiles()[getPixelX() >> 4][getPixelY() >> 4] = null;
		}

	}
}