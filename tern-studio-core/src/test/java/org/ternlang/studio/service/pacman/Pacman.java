package org.ternlang.studio.service.pacman;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

/* This class contains the entire game... most of the game logic is in the Board class but this
   creates the gui and captures mouse and keyboard input, as well as controls the game states */
public class Pacman extends JFrame {

   /* Create a new board */
   private Board board;
   private BoardPanel panel;
   private GameController controller;

   /* This timer is used to do request new frames be drawn */
   private Timer frameTimer;
   
   /*
    * These timers are used to kill title, game over, and victory screens after
    * a set idle period (5 seconds)
    */
   private long titleTimer = -1;
   private long timer = -1;
   
   /* This constructor creates the entire game essentially */
   public Pacman() {
      this.board = new Board();
      this.panel = new BoardPanel(board);
      this.controller = new GameController(this, board);
      this.frameTimer = new Timer(30, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            stepFrame(false);
         }
         
      });
      
      panel.requestFocus();
      //f.setSize(420, 460);
      setSize(1000, 1000);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      /* Add the board to the frame */
      add(panel, BorderLayout.CENTER);

      /* Set listeners for mouse actions and button clicks */
      panel.addMouseListener(controller);
      panel.addKeyListener(controller);

      /* Make frame visible, disable resizing */
      setVisible(true);
      setResizable(false);

      /* Set the New flag to 1 because this is a new game */
      board.phase = Phase.MENU;

      /* Manually call the first frameStep to initialize the game. */
      stepFrame(true);

      panel.requestFocus();
   }
   
   public void start() {
      /* Start the timer */
      frameTimer.start();
   }

   /*
    * This repaint function repaints only the parts of the screen that may have
    * changed. Namely the area around every player ghost and the menu bars
    */
   public void repaint() {
      if(board != null) {
         if (board.player.teleport) {
            panel.repaint(board.player.lastX - 20, board.player.lastY - 20, 80, 80);
            board.player.teleport = false;
         }
         panel.repaint();
      }
   }

   /* Steps the screen forward one frame */
   public void stepFrame(boolean pausePlay) {
      /*
       * If we aren't on a special screen than the timers can be set to -1 to
       * disable them
       */
      if (!board.titleScreen && !board.winScreen && !board.overScreen) {
         timer = -1;
         titleTimer = -1;
      }

      /*
       * If we are playing the dying animation, keep advancing frames until the
       * animation is complete
       */
      if (board.dying > 0) {
         panel.repaint();
         return;
      }

      /*
       * New can either be specified by the New parameter in stepFrame function
       * call or by the state of b.New. Update New accordingly
       */
      pausePlay = pausePlay || board.phase.isPaused();

      /*
       * If this is the title screen, make sure to only stay on the title screen
       * for 5 seconds. If after 5 seconds the user hasn't started a game, start
       * up demo mode
       */
//      if (board.titleScreen) {
//         if (titleTimer == -1) {
//            titleTimer = System.currentTimeMillis();
//         }
//
//         long currTime = System.currentTimeMillis();
//         if (currTime - titleTimer >= 5000) {
//            board.titleScreen = false;
//            board.demo = true;
//            titleTimer = -1;
//         }
//         panel.repaint();
//         return;
//      }

      /*
       * If this is the win screen or game over screen, make sure to only stay
       * on the screen for 5 seconds. If after 5 seconds the user hasn't pressed
       * a key, go to title screen
       */
      /*else*/ if (board.winScreen || board.overScreen) {
         if (timer == -1) {
            timer = System.currentTimeMillis();
         }

         long currTime = System.currentTimeMillis();
         if (currTime - timer >= 5000) {
            board.winScreen = false;
            board.overScreen = false;
            board.titleScreen = true;
            timer = -1;
         }
         panel.repaint();
         return;
      }

      /*
       * If we have a normal game state, move all pieces and update pellet
       * status
       */
      if (!pausePlay) {
         /*
          * The pacman player has two functions, demoMove if we're in demo mode
          * and move if we're in user playable mode. Call the appropriate one
          * here
          */
         if (board.demo) {
            board.player.demoMove();
         } else {
            board.player.move();
         }

         /* Also move the ghosts, and update the pellet states */
         board.ghost1.move();
         board.ghost2.move();
         board.ghost3.move();
         board.ghost4.move();
         board.player.updatePellet();
         board.ghost1.updatePellet();
         board.ghost2.updatePellet();
         board.ghost3.updatePellet();
         board.ghost4.updatePellet();
      }

      /*
       * We either have a new game or the user has died, either way we have to
       * reset the board
       */
      if (board.stopped || pausePlay) {
         /* Temporarily stop advancing frames */
         frameTimer.stop();

         /* If user is dying ... */
         while (board.dying > 0) {
            /* Play dying animation. */
            stepFrame(false);
         }

         /*
          * Move all game elements back to starting positions and orientations
          */
         board.player.currDirection = 'L';
         board.player.direction = 'L';
         board.player.desiredDirection = 'L';
         board.player.x = 200;
         board.player.y = 300;
         board.ghost1.x = 180;
         board.ghost1.y = 180;
         board.ghost2.x = 200;
         board.ghost2.y = 180;
         board.ghost3.x = 220;
         board.ghost3.y = 180;
         board.ghost4.x = 220;
         board.ghost4.y = 180;

         /* Advance a frame to display main state */
         //panel.repaint(0, 0, 600, 600);
         panel.repaint();
         
         /* Start advancing frames once again */
         board.stopped = false;
         frameTimer.start();
      }
      /* Otherwise we're in a normal state, advance one frame */
      else {
         repaint();
      }
   }
}
