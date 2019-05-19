package org.ternlang.studio.service.pacman;

import java.awt.Graphics;

import javax.swing.JPanel;

public class BoardPanel extends JPanel {

   private final Board b;
   
   public BoardPanel(Board b) {
      this.b = b;
   }
   
   public void paint(Graphics g) {
      b.draw(g);
   }
}
