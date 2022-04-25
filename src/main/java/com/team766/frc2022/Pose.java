package com.team766.frc2022;

public class Pose {
	public double m_x;
	public double m_y;
	public double m_heading;

	public Pose(double x, double y, double heading) {
		m_x = x;
		m_y = y;
		m_heading = heading;
	}

	public String toString() {
		return String.format("(%f, %f, %f)", m_x, m_y, m_heading);
	}
}