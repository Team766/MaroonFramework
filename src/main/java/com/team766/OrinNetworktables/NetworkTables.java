package com.team766.framework.Orin-Networktables;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTable;

public class NetworkTables {
	NetworkTableInstance inst = NetworkTableInstance.getDefault();
	NetworkTable table = inst.getTable("/SmartDashboard");



	public double getData(String request) {
		NetworkTableEntry temp = table.getEntry(request);
		double value = temp.getDouble(Double.MAX_VALUE);
		return value;
	}
}
