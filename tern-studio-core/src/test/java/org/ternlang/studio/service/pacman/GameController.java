package org.ternlang.studio.service.pacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController extends MouseAdapter implements KeyListener {

   private final Pacman p;
   private final Board b;
   
   public GameController(Pacman p, Board b) {
      this.b = b;
      this.p = p;
   }

   /* Handles user key presses */
   @Override
   public void keyPressed(KeyEvent e) {
      /* Pressing a key in the title screen starts a game */
      if (b.titleScreen) {
         b.titleScreen = false;
         b.phase = Phase.START;
         return;
      }
      /*
       * Pressing a key in the win screen or game over screen goes to the title
       * screen
       */
      else if (b.winScreen || b.overScreen) {
         b.titleScreen = true;
         b.winScreen = false;
         b.overScreen = false;
         return;
      }
      /*
       * Pressing a key during a demo kills the demo mode and starts a new game
       */
      else if (b.demo) {
         b.demo = false;
         /* Stop any pacman eating sounds */
         b.sounds.nomNomStop();
         b.phase = Phase.DEMO;
         return;
      }

      /* Otherwise, key presses control the player! */
      switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:
         b.player.desiredDirection = 'L';
         break;
      case KeyEvent.VK_RIGHT:
         b.player.desiredDirection = 'R';
         break;
      case KeyEvent.VK_UP:
         b.player.desiredDirection = 'U';
         break;
      case KeyEvent.VK_DOWN:
         b.player.desiredDirection = 'D';
         break;
      }

      p.repaint();
   }

   /*
    * This function detects user clicks on the menu items on the bottom of the
    * screen
    */
   @Override
   public void mousePressed(MouseEvent e) {
      if (b.titleScreen || b.winScreen || b.overScreen) {
         /* If we aren't in the game where a menu is showing, ignore clicks */
         return;
      }

      /* Get coordinates of click */
      int x = e.getX();
      int y = e.getY();
      if (400 <= y && y <= 460) {
         if (100 <= x && x <= 150) {
            /* New game has been clicked */
            b.phase = Phase.START;
         } else if (180 <= x && x <= 300) {
            /* Clear high scores has been clicked */
            b.score.clearHighScores();
         } else if (350 <= x && x <= 420) {
            /* Exit has been clicked */
            System.exit(0);
         }
      }
   }

   @Override
   public void keyTyped(KeyEvent e) {
      if (b.titleScreen) {
         b.titleScreen = false;
         b.phase = Phase.START;
         return;
      }
   }

   @Override
   public void keyReleased(KeyEvent e) {
   }
}
