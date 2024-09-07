package main.java.com.team766.OrinNetworktables;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTable;

public class NetworkTables {
	NetworkTableInstance inst = NetworkTableInstance.getDefault();
	NetworkTable table = inst.getTable("/SmartDashboard");



	public double getDouble(String request) {
		NetworkTableEntry temp = table.getEntry(request);
		double value = temp.getDouble(Double.MAX_VALUE);
		return value;
	}

    public String getString(String request) {
        NetworkTableEntry temp = table.getEntry(request);
		String value = temp.getDouble(Double.MAX_VALUE);
		return value;
    }

    public array getArray(String request) {
        NetworkTableEntry temp = table.getEntry(request);
		array value = temp.getDouble(Double.MAX_VALUE);
		return value;
    }
    
}