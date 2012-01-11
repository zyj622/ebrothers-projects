package org.cocos2d.levelhelper.nodes;

import java.util.HashMap;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;

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

	private int type;
	private Joint joint;
	private int tag;
	private String uniqueName;
	private HashMap<String, Object> customUserValues;

	public static LHJoint jointWithUniqueName(String name, int tag, int type,
			Joint _boxJoint) {
		LHJoint pobJoint = new LHJoint(tag, type, _boxJoint);
		if (pobJoint != null && pobJoint.initWithUniqueName(name)) {
			return pobJoint;
		}
		return null;
	}

	public LHJoint(int _tag, int _type, Joint _boxJoint) {
		tag = _tag;
		type = _type;
		joint = _boxJoint;
		customUserValues = new HashMap<String, Object>();
	}

	public void setCustomValueWithKey(String key, Object value) {
		customUserValues.put(key, value);
	}

	public Object customValueWithKey(String key) {
		return customUserValues.get(key);
	}

	public int getType() {
		return type;
	}

	public Joint getJoint() {
		return joint;
	}

	public boolean initWithUniqueName(String name) {
		uniqueName = name;
		return true;
	}

	public int getTag() {
		return tag;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public boolean removeJointFromWorld() {
		if (joint != null) {
			Body body = joint.getBodyA();
			if (body == null) {
				body = joint.getBodyB();
				if (body == null)
					return false;
			}
			World _world = body.getWorld();
			if (_world == null)
				return false;
			_world.destroyJoint(joint);
			return true;
		}
		return false;
	}
}
