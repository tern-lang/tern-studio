package org.ternlang.studio.service.physics;

public class Vector2d {

	private float x;
	private float y;

	public Vector2d()
	{
		this.setX(0);
		this.setY(0);
	}

	public Vector2d(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public void set(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}


	public float dot(Vector2d v2)
	{
		float result = 0.0f;
		result = this.getX() * v2.getX() + this.getY() * v2.getY();
		return result;
	}

	public float getLength()
	{
		return (float)Math.sqrt(getX()*getX() + getY()*getY());
	}

	public float getDistance(Vector2d v2)
	{
		return (float) Math.sqrt((v2.getX() - getX()) * (v2.getX() - getX()) + (v2.getY() - getY()) * (v2.getY() - getY()));
	}


	public Vector2d add(Vector2d v2)
	{
		Vector2d result = new Vector2d();
		result.setX(getX() + v2.getX());
		result.setY(getY() + v2.getY());
		return result;
	}

	public Vector2d subtract(Vector2d v2)
	{
		Vector2d result = new Vector2d();
		result.setX(this.getX() - v2.getX());
		result.setY(this.getY() - v2.getY());
		return result;
	}

	public Vector2d multiply(float scaleFactor)
	{
		Vector2d result = new Vector2d();
		result.setX(this.getX() * scaleFactor);
		result.setY(this.getY() * scaleFactor);
		return result;
	}

	public Vector2d normalize()
	{
		float len = getLength();
		if (len != 0.0f)
		{
			this.setX(this.getX() / len);
			this.setY(this.getY() / len);
		}
		else
		{
			this.setX(0.0f);
			this.setY(0.0f);
		}

		return this;
	}

	public String toString()
	{
		return "X: " + getX() + " Y: " + getY();
	}


}
