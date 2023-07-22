package com.team766.web;

import java.util.Map;

public class DriverInterface implements WebServer.Handler {

	AutonomousSelector autonomousSelector;

	public DriverInterface(final AutonomousSelector autonomousSelectorParam) {
		this.autonomousSelector = autonomousSelectorParam;
	}

	@Override
	public String endpoint() {
		return "/driver";
	}

	@Override
	public String title() {
		return "Driver Interface";
	}

	@Override
	public String handle(final Map<String, Object> params) {
		return Dashboard.makeDashboardPage()
			+ LogViewer.makeAllErrorsPage()
			+ autonomousSelector.handle(params);
	}

}
