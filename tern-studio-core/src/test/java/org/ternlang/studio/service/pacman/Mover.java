package org.ternlang.studio.service.pacman;

/* Both Player and Ghost inherit Mover.  Has generic functions relevant to both*/
class Mover {
   /* Framecount is used to count animation frames */
   protected int frameCount = 0;

   /* State contains the game map */
   boolean[][] state;

   /*
    * gridSize is the size of one square in the game. max is the height/width of
    * the game. increment is the speed at which the object moves, 1 increment
    * per move() call
    */
   int gridSize;
   int max;
   int increment;

   /* Generic constructor */
   public Mover() {
      gridSize = 20;
      increment = 4;
      max = 400;
      state = new boolean[20][20];
      for (int i = 0; i < 20; i++) {
         for (int j = 0; j < 20; j++) {
            state[i][j] = false;
         }
      }
   }

   /* Updates the state information */
   public void updateState(boolean[][] state) {
      for (int i = 0; i < 20; i++) {
         for (int j = 0; j < 20; j++) {
            this.state[i][j] = state[i][j];
         }
      }
   }

   /* Determines if a set of coordinates is a valid destination. */
   public boolean isValidDest(int x, int y) {
      /*
       * The first statements check that the x and y are inbounds. The last
       * statement checks the map to see if it's a valid location
       */
      if ((((x) % 20 == 0) || ((y) % 20) == 0) && 20 <= x && x < 400 && 20 <= y && y < 400 && state[x / 20 - 1][y / 20 - 1]) {
         return true;
      }
      return false;
   }
}