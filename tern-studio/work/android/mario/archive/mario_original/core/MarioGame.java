package mario.core;

import tern.android.game.framework.Screen;
import tern.android.game.framework.gfx.AndroidGame;

import mario.screens.SplashLoadingScreen;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

/**
 * Main Entry Class for the game (Only activity used in game)
 * 
 * @author mahesh
 *
 */
public class MarioGame extends AndroidGame {

   public final MarioResourceManager resourceManager;
   public final MarioSoundManager soundManager;
   public static final int QUIT_GAME_DIALOG = 0;
   
   public MarioGame(Activity activity, float w, float h, boolean isLandscape) {
      super(activity, w, h, isLandscape);
      this.soundManager = new MarioSoundManager(this);
      this.resourceManager = new MarioResourceManager(this);
   }

   public Screen getStartScreen() {
      return new SplashLoadingScreen(this);
   }

   @Override
   public void onBackPressed() {
      getCurrentScreen().onBackPressed();
   }

   @Override
   public void onResume() {
      super.onResume();
      if (soundManager != null)
         soundManager.playMusic();

   }

   @Override
   public void onPause() {
      super.onPause();
      if (soundManager != null)
         soundManager.pauseMusic();
   }

   @Override
   public Dialog onCreateDialog(int id) {
      Dialog dialog = null;
      if (id == QUIT_GAME_DIALOG) {

         dialog = new AlertDialog.Builder(getContext()).setTitle("Quit Game").setPositiveButton("Return to main menu?", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               getContext().finish();
            }
         }).setNegativeButton("Quit", null).setMessage("Cancel").create();
      }
      return dialog;
   }

}
