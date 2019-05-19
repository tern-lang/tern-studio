package org.ternlang.studio.service.pacman;

import java.awt.Image;

public interface ImageMap {
   
   Image PACMAN = ImageLoader.loadImage("images/pacman.jpg");
   Image PACMAN_UP = ImageLoader.loadImage("images/pacmanup.jpg");
   Image PACMAN_DOWN = ImageLoader.loadImage("images/pacmandown.jpg");
   Image PACMAN_LEFT = ImageLoader.loadImage("images/pacmanleft.jpg");
   Image PACMAN_RIGHT = ImageLoader.loadImage("images/pacmanright.jpg");
   Image GHOST_10 = ImageLoader.loadImage("images/ghost10.jpg");
   Image GHOST_20 = ImageLoader.loadImage("images/ghost20.jpg");
   Image GHOST_30 = ImageLoader.loadImage("images/ghost30.jpg");
   Image GHOST_40 = ImageLoader.loadImage("images/ghost40.jpg");
   Image GHOST_11 = ImageLoader.loadImage("images/ghost11.jpg");
   Image GHOST_21 = ImageLoader.loadImage("images/ghost21.jpg");
   Image GHOST_31 = ImageLoader.loadImage("images/ghost31.jpg");
   Image GHOST_41 = ImageLoader.loadImage("images/ghost41.jpg");
   Image SPLASH = ImageLoader.loadImage("images/titleScreen.jpg");
   Image GAME_OVER = ImageLoader.loadImage("images/gameOver.jpg");
   Image WINNER = ImageLoader.loadImage("images/winScreen.jpg");
}
