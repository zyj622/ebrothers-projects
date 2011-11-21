package com.ebrothers.linerunner.common;

public interface Constants {
	// Pixel to meters ratio. Box2D uses meters as the unit for measurement.
	// This ratio defines how many pixels correspond to 1 Box2D "meter"
	// Box2D is optimized for objects of 1x1 meter therefore it makes sense
	// to define the ratio so that your most common object type is 1x1
	// meter.
	public static final float PTM_RATIO = 32.0f;
}
