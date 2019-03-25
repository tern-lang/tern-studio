package org.ternlang.studio.service.physics;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Ball implements Comparable<Ball>{

	public Vector2d velocity;
	public Vector2d position;
	private float mass;
	private float radius;
	private float angularVel;
	private float orientation = 45f;

	public Ball(float x, float y, float radius)
	{
		this(x, y, radius, 1);

	}

	public Ball(float x, float y, float radius, float mass)
	{
		this.velocity = new Vector2d(0, 0);
		this.position = new Vector2d(x, y);
		this.setMass(mass);
		this.setRadius(radius);
	}

	public Color getBallColor(float magnitude)
	{
		float maxMagnitude = 1000; // tweak this to get the right color range

		magnitude = magnitude < maxMagnitude ? magnitude : maxMagnitude;

		float H = (magnitude/maxMagnitude) * 0.38f;  // 0.4f = green
		float S = 0.98f;
		float B = 0.95f;

		return Color.getHSBColor(H, S, B);
	}


	public void draw(Graphics2D g2)
	{



		//g2.setColor(getBallColor(velocity.getLength()));
		//g2.drawOval((int) (position.getX() - getRadius()), (int) (position.getY() - getRadius()), (int) (2 * getRadius()) , (int) (2 * getRadius()) );
		g2.setColor(getBallColor(velocity.getLength()));
		g2.fillOval( (int) (position.getX() - getRadius()), (int) (position.getY() - getRadius()), (int) (2 * getRadius()) , (int) (2 * getRadius()) );

	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}


	// Deprecated, rolled this into checkCollision for effeciency.
	public boolean colliding(Ball ball)
	{
		float xd = position.getX() - ball.position.getX();
		float yd = position.getY() - ball.position.getY();

		float sumRadius = getRadius() + ball.getRadius();
		float sqrRadius = sumRadius * sumRadius;

		float distSqr = (xd * xd) + (yd * yd);

		if (distSqr <= sqrRadius)
		{
			return true;
		}

		return false;

	}

	public void resolveCollision(Ball ball)
	{

		// get the mtd
		Vector2d delta = (position.subtract(ball.position));
		float r = getRadius() + ball.getRadius();
		float dist2 = delta.dot(delta);

		if (dist2 > r*r) return; // they aren't colliding


		float d = delta.getLength();

		Vector2d mtd;
		if (d != 0.0f)
		{
			mtd = delta.multiply(((getRadius() + ball.getRadius())-d)/d); // minimum translation distance to push balls apart after intersecting

		}
		else // Special case. Balls are exactly on top of eachother.  Don't want to divide by zero.
		{
			d = ball.getRadius() + getRadius() - 1.0f;
			delta = new Vector2d(ball.getRadius() + getRadius(), 0.0f);

			mtd = delta.multiply(((getRadius() + ball.getRadius())-d)/d);
		}

		// resolve intersection
		float im1 = 1 / getMass(); // inverse mass quantities
		float im2 = 1 / ball.getMass();

		// push-pull them apart
		position = position.add(mtd.multiply(im1 / (im1 + im2)));
		ball.position = ball.position.subtract(mtd.multiply(im2 / (im1 + im2)));

		// impact speed
		Vector2d v = (this.velocity.subtract(ball.velocity));
		float vn = v.dot(mtd.normalize());

		// sphere intersecting but moving away from each other already
		if (vn > 0.0f) return;

		// collision impulse
		float i = (-(1.0f + Constants.restitution) * vn) / (im1 + im2);
		Vector2d impulse = mtd.multiply(i);

		// change in momentum
		this.velocity = this.velocity.add(impulse.multiply(im1));
		ball.velocity = ball.velocity.subtract(impulse.multiply(im2));

	}

	private void setMass(float mass) {
		this.mass = mass;
	}

	private float getMass() {
		return mass;
	}

	public int compareTo(Ball ball) {
		if (this.position.getX() - this.getRadius() > ball.position.getX() - ball.getRadius())
		{
			return 1;
		}
		else if (this.position.getX() - this.getRadius() < ball.position.getX() - ball.getRadius())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

}




