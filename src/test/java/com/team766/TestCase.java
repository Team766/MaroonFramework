package com.team766;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Scheduler;
import com.team766.hal.RobotProvider;
import com.team766.hal.mock.TestRobotProvider;

import org.junit.jupiter.api.Test;

public class TestCase {

	protected void setUp() {
		ConfigFileReader.instance = new ConfigFileReader("/Users/rcahoon/frc/MaroonFramework_3/simConfig.txt");
		RobotProvider.instance = new TestRobotProvider();

		Scheduler.getInstance().reset();
	}

	@Test
	public void test() {
		setUp();

		Scheduler.getInstance().run();
	}

}
