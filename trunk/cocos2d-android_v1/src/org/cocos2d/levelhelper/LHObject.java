package org.cocos2d.levelhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LHObject {

	public static final int TYPE_INT = 0;
	public static final int TYPE_FLOAT = 1;
	public static final int TYPE_BOOL = 2;
	public static final int TYPE_STRING = 3;
	public static final int TYPE_LH_DICT = 4;
	public static final int TYPE_LH_ARRAY = 5;
	public static final int TYPE_VOID_TYPE = 6;
	public static final int TYPE_UNKNOWN = -1;

	private Object _o;
	private int _type = TYPE_UNKNOWN;

	@SuppressWarnings("unchecked")
	public LHObject(Object o) {
		if (o instanceof Integer) {
			_o = o;
			_type = TYPE_INT;
		} else if (o instanceof Float) {
			_o = o;
			_type = TYPE_FLOAT;
		} else if (o instanceof String) {
			_o = o;
			_type = TYPE_STRING;
		} else if (o instanceof Map<?, ?>) {
			HashMap<String, LHObject> dict = new HashMap<String, LHObject>();
			dict.putAll((HashMap<String, LHObject>) o);
			_o = dict;
			_type = TYPE_LH_DICT;
		} else if (o instanceof List<?>) {
			ArrayList<LHObject> array = new ArrayList<LHObject>();
			array.addAll((ArrayList<LHObject>) o);
			_o = array;
			_type = TYPE_LH_ARRAY;
		} else if (o instanceof Boolean) {
			_o = o;
			_type = TYPE_BOOL;
		} else if (o instanceof Void) {
			_type = TYPE_VOID_TYPE;
		}
	}

	public LHObject(LHObject lhObject) {
		this(lhObject._o);
	}

	public int intValue() {
		assert (TYPE_INT == _type);
		return (Integer) _o;
	}

	public float floatValue() {
		assert (TYPE_FLOAT == _type);
		return (Float) _o;
	}

	public boolean boolValue() {
		assert (TYPE_BOOL == _type);
		return (Boolean) _o;
	}

	public String stringValue() {
		assert (TYPE_STRING == _type);
		return (String) _o;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<LHObject> arrayValue() {
		assert (TYPE_LH_ARRAY == _type);
		return (ArrayList<LHObject>) _o;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, LHObject> dictValue() {
		assert (TYPE_LH_DICT == _type);
		return (HashMap<String, LHObject>) _o;
	}

	public int type() {
		return _type;
	}
}
