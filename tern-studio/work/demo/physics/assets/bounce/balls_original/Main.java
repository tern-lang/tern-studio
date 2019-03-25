package org.ternlang.studio.service.physics;
import javax.swing.*;

public class Main {

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("BallBounce");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));


		BallPanel ballCanvas = new BallPanel();
		ControlPanel controlPanel = new ControlPanel(ballCanvas);

		frame.getContentPane().add(ballCanvas);
		frame.getContentPane().add(controlPanel);
		frame.pack();
		frame.setVisible(true);

		ballCanvas.start();

	}

}
