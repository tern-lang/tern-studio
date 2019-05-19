package org.ternlang.studio.service.physics;
import java.awt.*;
import java.util.Random;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.image.*;
import java.lang.Thread;

public class ControlPanel extends JPanel {

	private BallPanel mainPanel;
	private JButton resetButton;
	private JButton generateButton;
	private JButton scatterButton;
	private JSlider gravitySlider;
	private JSlider corSlider;
	
	public ControlPanel(BallPanel mainPanel)
	{
		this.setPreferredSize(new Dimension(200, 60));
		this.setMaximumSize(new Dimension(5000, 100));
		this.mainPanel = mainPanel;
		
		// instantiate controls
		resetButton = new JButton("Reset");
		generateButton = new JButton("Generate");
		scatterButton = new JButton("Scatter");
		
		gravitySlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 2000);
		gravitySlider.setBorder(BorderFactory.createTitledBorder("Gravity - " + gravitySlider.getValue() + "px/s"));
		gravitySlider.setMajorTickSpacing(200);
		gravitySlider.setMinorTickSpacing(100);
		gravitySlider.setPaintTicks(true);
		
		corSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 85);
		corSlider.setBorder(BorderFactory.createTitledBorder("Restitution - " + corSlider.getValue() + "%"));
		corSlider.setMajorTickSpacing(10);
		corSlider.setMinorTickSpacing(5);
		corSlider.setPaintTicks(true);
		
		// add controls to panel
		this.add(gravitySlider);
		this.add(corSlider);
		this.add(scatterButton);
		this.add(generateButton);
		this.add(resetButton);
		
		
		// wire up gui events
		ButtonHandler buttonHandler = new ButtonHandler();
		resetButton.addActionListener(buttonHandler);
		scatterButton.addActionListener(buttonHandler);
		generateButton.addActionListener(buttonHandler);
		
		SliderHandler sliderHandler = new SliderHandler();
		gravitySlider.addChangeListener(sliderHandler);
		
	}
	
	private class ButtonHandler implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			JButton source = (JButton)e.getSource();
			
			if (source == resetButton)
			{
				mainPanel.clearBalls();
			}
			
			if (source == generateButton)
			{
				mainPanel.generateBalls(100);
			}
			
			if (source == scatterButton)
			{
				mainPanel.scatterBalls();
			}
			
			
		}
	
	}
	
	private class SliderHandler implements ChangeListener
	{

		public void stateChanged(ChangeEvent e) 
		{
			
			JSlider source = (JSlider)e.getSource();
			
			if (source == gravitySlider)
			{
				source.setBorder(BorderFactory.createTitledBorder("Gravity - " + source.getValue() + "px/s"));
				mainPanel.setGravity(source.getValue());
			}
			
		}
	}	
}
