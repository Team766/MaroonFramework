package com.team766.hal.mock;

import com.team766.hal.PositionReader;

public class MockPositionSensor implements PositionReader {

	private double x = 0;
	private double y = 0;
	private double heading = 0;

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getHeading() {
		return heading;
	}

	public void setX(final double xParam) {
		this.x = xParam;
	}

	public void setY(final double yParam) {
		this.y = yParam;
	}

	public void setHeading(final double headingParam) {
		this.heading = headingParam;
	}

}