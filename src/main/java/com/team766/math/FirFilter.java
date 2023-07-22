package com.team766.math;

import java.util.stream.Collectors;

import com.team766.library.CircularBuffer;

public class FirFilter implements Filter {
	private CircularBuffer<Double> buffer;

	public FirFilter(final int bufferLength) {
		buffer = new CircularBuffer<>(bufferLength);
	}

	public void push(final double sample) {
		buffer.add(sample);
	}

	public double getValue() {
		return buffer.stream().collect(Collectors.averagingDouble(Double::doubleValue));
	}
}
