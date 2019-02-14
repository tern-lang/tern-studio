package mario.screens;

import org.ternlang.android.game.framework.Game;
import org.ternlang.android.game.framework.Graphics;
import org.ternlang.android.game.framework.Screen;
import org.ternlang.android.game.framework.Graphics.ImageFormat;

import mario.core.Assets;

public class SplashLoadingScreen extends Screen {
    
	public SplashLoadingScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
       Graphics g = game.getGraphics();
       Assets.splash= g.newImage("backgrounds/splash.png",ImageFormat.RGB565);
       game.setScreen(new LoadingScreen(game));
    }

    @Override
    public void paint(float deltaTime) {
    	 
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
    	
    }

    @Override
    public void onBackPressed() {

    }
}