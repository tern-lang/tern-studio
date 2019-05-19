package org.ternlang.studio.service.pacman;

/* Drew Schuster */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/*
 * This board class contains the player, ghosts, pellets, and most of the game
 * logic.
 */
public class Board {

   /* Used to call sound effects */
   GameSounds sounds;
   /* Initialize the player and ghosts */
   Player player;
   Ghost ghost1;
   Ghost ghost2;
   Ghost ghost3;
   Ghost ghost4;
   Score score;
   Phase phase;
   
   /* Timer is used for playing sound effects and animations */
   long timer = System.currentTimeMillis();

   /*
    * Dying is used to count frames in the dying animation. If it's non-zero,
    * pacman is in the process of dying
    */
   int dying = 0;

   /* Score information */
   int currScore;


   int numLives = 2;

   /* Contains the game map, passed to player and ghosts */
   boolean[][] state;

   /* Contains the state of all pellets */
   boolean[][] pellets;

   /* Game dimensions */
   int gridSize;
   int max;

   /* State flags */
   boolean stopped;
   boolean titleScreen;
   boolean winScreen = false;
   boolean overScreen = false;
   boolean demo = false;

   int lastPelletEatenX = 0;
   int lastPelletEatenY = 0;

   /* This is the font used for the menus */
   Font font;

   /* Constructor initializes state flags etc. */
   public Board() {
      this.font = new Font("Monospaced", Font.BOLD, 12);
      this.sounds = new GameSounds();
      /* Initialize the player and ghosts */
      this.player = new Player(200, 300);
      this.ghost1 = new Ghost(180, 180);
      this.ghost2 = new Ghost(200, 180);
      this.ghost3 = new Ghost(220, 180);
      this.ghost4 = new Ghost(220, 180);
      this.score = new Score();
      this.currScore = 0;
      this.stopped = false;
      this.max = 400;
      this.gridSize = 20;
      this.phase = Phase.PLAY;
      this.titleScreen = true;
      
      reset();
   }



   /* Reset occurs on a new game */
   public void reset() {
      numLives = 2;
      state = new boolean[20][20];
      pellets = new boolean[20][20];

      /* Clear state and pellets arrays */
      for (int i = 0; i < 20; i++) {
         for (int j = 0; j < 20; j++) {
            state[i][j] = true;
            pellets[i][j] = true;
         }
      }

      /* Handle the weird spots with no pellets */
      for (int i = 5; i < 14; i++) {
         for (int j = 5; j < 12; j++) {
            pellets[i][j] = false;
         }
      }
      pellets[9][7] = false;
      pellets[8][8] = false;
      pellets[9][8] = false;
      pellets[10][8] = false;

   }

   /*
    * Function is called during drawing of the map. Whenever the a portion of
    * the map is covered up with a barrier, the map and pellets arrays are
    * updated accordingly to note that those are invalid locations to travel or
    * put pellets
    */
   private void updateMap(int x, int y, int width, int height) {
      for (int i = x / gridSize; i < x / gridSize + width / gridSize; i++) {
         for (int j = y / gridSize; j < y / gridSize + height / gridSize; j++) {
            state[i - 1][j - 1] = false;
            pellets[i - 1][j - 1] = false;
         }
      }
   }
   
   
   public void draw(Graphics gr) {
      Rectangle rect = gr.getClipBounds();
      BufferedImage original = new BufferedImage(420, 460, BufferedImage.TYPE_INT_ARGB);
      Graphics graphics = original.createGraphics();
   
      drawGame(graphics);
      gr.drawImage(original, 0, 0, rect.width, rect.height, null);
   }
   
   private void drawGame(Graphics g) {
      /*
       * If we're playing the dying animation, don't update the entire screen.
       * Just kill the pacman
       */
      if (dying > 0) {
         drawDead(g);
      } else if (titleScreen) {
         /* If this is the title screen, draw the title screen and return */
         drawTitleScreen(g);
      } else if (winScreen) {
         /* If this is the win screen, draw the win screen and return */
         drawWin(g);
      } else if (overScreen) {
         /* If this is the game over screen, draw the game over screen and return */
         drawGameOver(g);
      } else {
   
         /* If need to update the high scores, redraw the top menu bar */
         if (score.isClearHighScore()) {
            drawScore(g);
         }
      
         /* Game initialization */
         if (phase == Phase.START) {
            drawBeginGame(g);
         }
         /* Second frame of new game */
         else if (phase == Phase.STARTING) {
            phase = Phase.PLAY;
         }
         /* Third frame of new game */
         else if (phase == Phase.PLAY) {
            phase = Phase.PLAYING;
            /* Play the newGame sound effect */
            sounds.newGame();
            timer = System.currentTimeMillis();
            return;
         } else if (phase == Phase.PLAYING) {
            // not needed but prevents any bad paints
            drawBoard(g);
            drawPellets(g);
            g.setColor(Color.YELLOW);
            g.setFont(font);
            g.drawString("Score: " + (currScore) + "\t High Score: " + score.getHighScore(), 20, 10);         
         }
      
         /* oops is set to true when pacman has lost a life */
         drawCollision(g);
      
         /* Delete the players and ghosts */
         drawClearBoard(g);
      
         /* Eat pellets */
         drawEatPellet(g);
      
         /* Replace pellets that have been run over by ghosts */
         drawRunOverPellet(g);
      
         /* Draw the ghosts */
         drawGhosts(g);
      
         /* Draw the pacman */
         drawPlayer(g);
      
         /*
          * Draw the border around the game in case it was overwritten by ghost
          * movement or something
          */
         drawRect(g);
      }
   }

   private void drawClearBoard(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(player.lastX, player.lastY, 20, 20);
      g.fillRect(ghost1.lastX, ghost1.lastY, 20, 20);
      g.fillRect(ghost2.lastX, ghost2.lastY, 20, 20);
      g.fillRect(ghost3.lastX, ghost3.lastY, 20, 20);
      g.fillRect(ghost4.lastX, ghost4.lastY, 20, 20);
   }

   private void drawCollision(Graphics g) {
      boolean collision = isCollision();
   
      /* Kill the pacman */
      if (collision && !stopped) {
         /* 4 frames of death */
         dying = 4;
   
         /* Play death sound effect */
         sounds.death();
         /* Stop any pacman eating sounds */
         sounds.nomNomStop();
   
         /*
          * Decrement lives, update screen to reflect that. And set appropriate
          * flags and timers
          */
         numLives--;
         stopped = true;
         drawLives(g);
         timer = System.currentTimeMillis();
      }
   }

   private void drawEatPellet(Graphics g) {
      if (pellets[player.pelletX][player.pelletY] && phase == Phase.PLAYING) {
         lastPelletEatenX = player.pelletX;
         lastPelletEatenY = player.pelletY;
     
         /* Play eating sound */
         sounds.nomNom();
     
         /* Increment pellets eaten value to track for end game */
         player.pelletsEaten++;
     
         /* Delete the pellet */
         pellets[player.pelletX][player.pelletY] = false;
     
         /* Increment the score */
         currScore += 50;
     
         /* Update the screen to reflect the new score */
         g.setColor(Color.BLACK);
         g.fillRect(0, 0, 600, 20);
         g.setColor(Color.YELLOW);
         g.setFont(font);
         if (demo)
            g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: " + score.getHighScore(), 20, 10);
         else
            g.drawString("Score: " + (currScore) + "\t High Score: " + score.getHighScore(), 20, 10);
         
         if(isLastPelletEaten()) {
            return;
         }
      }
   
      /* If we moved to a location without pellets, stop the sounds */
      else if ((player.pelletX != lastPelletEatenX || player.pelletY != lastPelletEatenY) || player.stopped) {
         /* Stop any pacman eating sounds */
         sounds.nomNomStop();
      }
   }

   private void drawRunOverPellet(Graphics g) {
      if (pellets[ghost1.lastPelletX][ghost1.lastPelletY])
         fillPellet(ghost1.lastPelletX, ghost1.lastPelletY, g);
      if (pellets[ghost2.lastPelletX][ghost2.lastPelletY])
         fillPellet(ghost2.lastPelletX, ghost2.lastPelletY, g);
      if (pellets[ghost3.lastPelletX][ghost3.lastPelletY])
         fillPellet(ghost3.lastPelletX, ghost3.lastPelletY, g);
      if (pellets[ghost4.lastPelletX][ghost4.lastPelletY])
         fillPellet(ghost4.lastPelletX, ghost4.lastPelletY, g);
   }

   private void drawRect(Graphics g) {
      g.setColor(Color.WHITE);
      g.drawRect(19, 19, 382, 382);
   }

   private void drawPlayer(Graphics g) {
      if (player.frameCount < 5) {
         /* Draw mouth closed */
         g.drawImage(ImageMap.PACMAN, player.x, player.y, Color.BLACK, null);
      } else {
         /* Draw mouth open in appropriate direction */
         if (player.frameCount >= 10)
            player.frameCount = 0;
   
         switch (player.currDirection) {
         case 'L':
            g.drawImage(ImageMap.PACMAN_LEFT, player.x, player.y, Color.BLACK, null);
            break;
         case 'R':
            g.drawImage(ImageMap.PACMAN_RIGHT, player.x, player.y, Color.BLACK, null);
            break;
         case 'U':
            g.drawImage(ImageMap.PACMAN_UP, player.x, player.y, Color.BLACK, null);
            break;
         case 'D':
            g.drawImage(ImageMap.PACMAN_DOWN, player.x, player.y, Color.BLACK, null);
            break;
         }
      }
   }

   private void drawGhosts(Graphics g) {
      if (player.frameCount < 5) {
         /* Draw first frame of ghosts */
         g.drawImage(ImageMap.GHOST_10, ghost1.x, ghost1.y, Color.BLACK, null);
         g.drawImage(ImageMap.GHOST_20, ghost2.x, ghost2.y, Color.BLACK, null);
         g.drawImage(ImageMap.GHOST_30, ghost3.x, ghost3.y, Color.BLACK, null);
         g.drawImage(ImageMap.GHOST_40, ghost4.x, ghost4.y, Color.BLACK, null);
         player.frameCount++;
      } else {
         /* Draw second frame of ghosts */
         g.drawImage(ImageMap.GHOST_11, ghost1.x, ghost1.y, Color.BLACK, null);
         g.drawImage(ImageMap.GHOST_21, ghost2.x, ghost2.y, Color.BLACK, null);
         g.drawImage(ImageMap.GHOST_31, ghost3.x, ghost3.y, Color.BLACK, null);
         g.drawImage(ImageMap.GHOST_41, ghost4.x, ghost4.y, Color.BLACK, null);
         if (player.frameCount >= 10)
            player.frameCount = 0;
         else
            player.frameCount++;
      }
   }
   

   private void drawBeginGame(Graphics g) {
      reset();
      player = new Player(200, 300);
      ghost1 = new Ghost(180, 180);
      ghost2 = new Ghost(200, 180);
      ghost3 = new Ghost(220, 180);
      ghost4 = new Ghost(220, 180);
      currScore = 0;
      drawBoard(g);
      drawPellets(g);
      drawLives(g);
      /* Send the game map to player and all ghosts */
      player.updateState(state);
      /* Don't let the player go in the ghost box */
      player.state[9][7] = false;
      ghost1.updateState(state);
      ghost2.updateState(state);
      ghost3.updateState(state);
      ghost4.updateState(state);

      /* Draw the top menu bar */
      g.setColor(Color.YELLOW);
      g.setFont(font);
      if (demo) {
         g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: " + score.getHighScore(), 20, 10);
      } else {
         g.drawString("Score: " + (currScore) + "\t High Score: " + score.getHighScore(), 20, 10);
      }
      phase = Phase.STARTING;
   }

   private void drawScore(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 600, 18);
      g.setColor(Color.YELLOW);
      g.setFont(font);
      score.setClearHighScores(false);
      if (demo)
         g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: " + score.getHighScore(), 20, 10);
      else
         g.drawString("Score: " + (currScore) + "\t High Score: " + score.getHighScore(), 20, 10);
   }

   private void drawGameOver(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 600, 600);
      g.drawImage(ImageMap.GAME_OVER, 0, 0, Color.BLACK, null);
      phase = Phase.GAME_OVER;
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
   }

   private void drawWin(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 600, 600);
      g.drawImage(ImageMap.WINNER, 0, 0, Color.BLACK, null);
      phase = Phase.WIN;
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
   }

   private void drawTitleScreen(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 600, 600);
      g.drawImage(ImageMap.SPLASH, 0, 0, Color.BLACK, null);
  
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
      phase = Phase.MENU;
   }

   private void drawDead(Graphics g) {
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
  
      /* Draw the pacman */
      g.drawImage(ImageMap.PACMAN, player.x, player.y, Color.BLACK, null);
      g.setColor(Color.BLACK);
  
      /* Kill the pacman */
      if (dying == 4)
         g.fillRect(player.x, player.y, 20, 7);
      else if (dying == 3)
         g.fillRect(player.x, player.y, 20, 14);
      else if (dying == 2)
         g.fillRect(player.x, player.y, 20, 20);
      else if (dying == 1) {
         g.fillRect(player.x, player.y, 20, 20);
      }
  
      /*
       * Take .1 seconds on each frame of death, and then take 2 seconds for
       * the final frame to allow for the sound effect to end
       */
      long currTime = System.currentTimeMillis();
      long temp;
      if (dying != 1)
         temp = 100;
      else
         temp = 2000;
      /* If it's time to draw a new death frame... */
      if (currTime - timer >= temp) {
         dying--;
         timer = currTime;
         /* If this was the last death frame... */
         if (dying == 0) {
            if (numLives == -1) {
               /* Demo mode has infinite lives, just give it more lives */
               if (demo)
                  numLives = 2;
               else {
                  /*
                   * Game over for player. If relevant, update high score.
                   * Set gameOver flag
                   */
                  if (currScore > score.getHighScore()) {
                     score.updateScore(currScore);
                  }
                  overScreen = true;
               }
            }
         }
      }
   }
   /*
    * Draws the appropriate number of lives on the bottom left of the screen.
    * Also draws the menu
    */
   private void drawLives(Graphics g) {
      g.setColor(Color.BLACK);

      /* Clear the bottom bar */
      g.fillRect(0, max + 5, 600, gridSize);
      g.setColor(Color.YELLOW);
      for (int i = 0; i < numLives; i++) {
         /* Draw each life */
         g.fillOval(gridSize * (i + 1), max + 5, gridSize, gridSize);
      }
      /* Draw the menu items */
      g.setColor(Color.YELLOW);
      g.setFont(font);
      g.drawString("Reset", 100, max + 5 + gridSize);
      g.drawString("Clear High Scores", 180, max + 5 + gridSize);
      g.drawString("Exit", 350, max + 5 + gridSize);
   }

   /*
    * This function draws the board. The pacman board is really complicated and
    * can only feasibly be done manually. Whenever I draw a wall, I call
    * updateMap to invalidate those coordinates. This way the pacman and ghosts
    * know that they can't traverse this area
    */
   private void drawBoard(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 600, 600);
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 420, 420);

      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 20, 600);
      g.fillRect(0, 0, 600, 20);
      drawRect(g);
      g.setColor(Color.BLUE);

      g.fillRect(40, 40, 60, 20);
      updateMap(40, 40, 60, 20);
      g.fillRect(120, 40, 60, 20);
      updateMap(120, 40, 60, 20);
      g.fillRect(200, 20, 20, 40);
      updateMap(200, 20, 20, 40);
      g.fillRect(240, 40, 60, 20);
      updateMap(240, 40, 60, 20);
      g.fillRect(320, 40, 60, 20);
      updateMap(320, 40, 60, 20);
      g.fillRect(40, 80, 60, 20);
      updateMap(40, 80, 60, 20);
      g.fillRect(160, 80, 100, 20);
      updateMap(160, 80, 100, 20);
      g.fillRect(200, 80, 20, 60);
      updateMap(200, 80, 20, 60);
      g.fillRect(320, 80, 60, 20);
      updateMap(320, 80, 60, 20);

      g.fillRect(20, 120, 80, 60);
      updateMap(20, 120, 80, 60);
      g.fillRect(320, 120, 80, 60);
      updateMap(320, 120, 80, 60);
      g.fillRect(20, 200, 80, 60);
      updateMap(20, 200, 80, 60);
      g.fillRect(320, 200, 80, 60);
      updateMap(320, 200, 80, 60);

      g.fillRect(160, 160, 40, 20);
      updateMap(160, 160, 40, 20);
      g.fillRect(220, 160, 40, 20);
      updateMap(220, 160, 40, 20);
      g.fillRect(160, 180, 20, 20);
      updateMap(160, 180, 20, 20);
      g.fillRect(160, 200, 100, 20);
      updateMap(160, 200, 100, 20);
      g.fillRect(240, 180, 20, 20);
      updateMap(240, 180, 20, 20);
      g.setColor(Color.BLUE);

      g.fillRect(120, 120, 60, 20);
      updateMap(120, 120, 60, 20);
      g.fillRect(120, 80, 20, 100);
      updateMap(120, 80, 20, 100);
      g.fillRect(280, 80, 20, 100);
      updateMap(280, 80, 20, 100);
      g.fillRect(240, 120, 60, 20);
      updateMap(240, 120, 60, 20);

      g.fillRect(280, 200, 20, 60);
      updateMap(280, 200, 20, 60);
      g.fillRect(120, 200, 20, 60);
      updateMap(120, 200, 20, 60);
      g.fillRect(160, 240, 100, 20);
      updateMap(160, 240, 100, 20);
      g.fillRect(200, 260, 20, 40);
      updateMap(200, 260, 20, 40);

      g.fillRect(120, 280, 60, 20);
      updateMap(120, 280, 60, 20);
      g.fillRect(240, 280, 60, 20);
      updateMap(240, 280, 60, 20);

      g.fillRect(40, 280, 60, 20);
      updateMap(40, 280, 60, 20);
      g.fillRect(80, 280, 20, 60);
      updateMap(80, 280, 20, 60);
      g.fillRect(320, 280, 60, 20);
      updateMap(320, 280, 60, 20);
      g.fillRect(320, 280, 20, 60);
      updateMap(320, 280, 20, 60);

      g.fillRect(20, 320, 40, 20);
      updateMap(20, 320, 40, 20);
      g.fillRect(360, 320, 40, 20);
      updateMap(360, 320, 40, 20);
      g.fillRect(160, 320, 100, 20);
      updateMap(160, 320, 100, 20);
      g.fillRect(200, 320, 20, 60);
      updateMap(200, 320, 20, 60);

      g.fillRect(40, 360, 140, 20);
      updateMap(40, 360, 140, 20);
      g.fillRect(240, 360, 140, 20);
      updateMap(240, 360, 140, 20);
      g.fillRect(280, 320, 20, 40);
      updateMap(280, 320, 20, 60);
      g.fillRect(120, 320, 20, 60);
      updateMap(120, 320, 20, 60);
      drawLives(g);
   }

   /* Draws the pellets on the screen */
   private void drawPellets(Graphics g) {
      g.setColor(Color.YELLOW);
      for (int i = 1; i < 20; i++) {
         for (int j = 1; j < 20; j++) {
            if (pellets[i - 1][j - 1])
               g.fillOval(i * 20 + 8, j * 20 + 8, 4, 4);
         }
      }
   }

   /*
    * Draws one individual pellet. Used to redraw pellets that ghosts have run
    * over
    */
   private void fillPellet(int x, int y, Graphics g) {
      g.setColor(Color.YELLOW);
      g.fillOval(x * 20 + 28, y * 20 + 28, 4, 4);
   }
   
   private boolean isLastPelletEaten() {
      /* If this was the last pellet */
      if (player.pelletsEaten == 173) {
         /* Demo mode can't get a high score */
         if (!demo) {
            if (currScore > score.getHighScore()) {
               score.updateScore(currScore);
            }
            winScreen = true;
         } else {
            titleScreen = true;
         }
         return true;
      }
      return false;
   }

   private boolean isCollision() {
      /* Detect collisions */
      if (player.x == ghost1.x && Math.abs(player.y - ghost1.y) < 10) {
         return true;
      }
      if (player.x == ghost2.x && Math.abs(player.y - ghost2.y) < 10) {
         return true;
      }
      if (player.x == ghost3.x && Math.abs(player.y - ghost3.y) < 10) {
         return true;
      }
      if (player.x == ghost4.x && Math.abs(player.y - ghost4.y) < 10) {
         return true;
      }
      if (player.y == ghost1.y && Math.abs(player.x - ghost1.x) < 10) {
         return true;
      }
      if (player.y == ghost2.y && Math.abs(player.x - ghost2.x) < 10) {
         return true;
      }
      if (player.y == ghost3.y && Math.abs(player.x - ghost3.x) < 10) {
         return true;
      }
      if (player.y == ghost4.y && Math.abs(player.x - ghost4.x) < 10) {
         return true;
      }
      return false;
   }

}
