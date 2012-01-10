package org.cocos2d.levelhelper;

import com.badlogic.gdx.physics.box2d.Joint;

public class LHJoint {
	public static final int LH_DISTANCE_JOINT = 0;
	public static final int LH_REVOLUTE_JOINT = 1;
	public static final int LH_PRISMATIC_JOINT = 2;
	public static final int LH_PULLEY_JOINT = 3;
	public static final int LH_GEAR_JOINT = 4;
	public static final int LH_WHEEL_JOINT = 5;
	public static final int LH_WELD_JOINT = 6;
	public static final int LH_ROPE_JOINT = 7;
	public static final int LH_FRICTION_JOINT = 8;
	public static final int LH_UNKNOWN_TYPE = 9;

	public Joint getJoint() {
		// TODO Auto-generated method stub
		return null;
	}

	public static LHJoint jointWithUniqueName(String stringValue, int tag,
			int type) {
		// TODO Auto-generated method stub
		return null;
	}

}
