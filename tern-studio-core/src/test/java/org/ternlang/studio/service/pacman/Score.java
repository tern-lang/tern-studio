package org.ternlang.studio.service.pacman;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Score {
   
   private static final String SCORE_FILE = "highScores.txt";
   
   private int highScore;

   /*
    * if the high scores have been cleared, we have to update the top of the
    * screen to reflect that
    */
   private boolean clearHighScores;
   
   public Score() {
      initHighScores();
   }
   
   public int getHighScore() {
      return highScore;
   }
   
   public void setClearHighScores(boolean clearHighScores) {
      this.clearHighScores = clearHighScores;
   }
   
   public boolean isClearHighScore() {
      return clearHighScores;
   }

   /* Reads the high scores file and saves it */
   public void initHighScores() {
      File file = new File(SCORE_FILE);
      Scanner sc;
      try {
         sc = new Scanner(file);
         highScore = sc.nextInt();
         sc.close();
      } catch (Exception e) {
      }
   }

   /*
    * Writes the new high score to a file and sets flag to update it on screen
    */
   public void updateScore(int score) {
      PrintWriter out;
      try {
         out = new PrintWriter(SCORE_FILE);
         out.println(score);
         out.close();
      } catch (Exception e) {
      }
      highScore = score;
      clearHighScores = true;
   }

   /* Wipes the high scores file and sets flag to update it on screen */
   public void clearHighScores() {
      PrintWriter out;
      try {
         out = new PrintWriter(SCORE_FILE);
         out.println("0");
         out.close();
      } catch (Exception e) {
      }
      highScore = 0;
      clearHighScores = true;
   }
}
