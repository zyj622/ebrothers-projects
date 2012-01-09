package org.cocos2d.levelhelper;

import org.cocos2d.types.CGPoint;

public class LHSettings {

	private static LHSettings _instance;

	public static LHSettings sharedInstance() {
		if (_instance == null) {
			_instance = new LHSettings();
		}
		return _instance;
	}

	public void setConvertRatio(CGPoint make) {
		// TODO Auto-generated method stub

	}

	public float lhPtmRatio() {
		// TODO Auto-generated method stub
		return 0;
	}

	public CGPoint convertRatio() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLhPtmRatio(float ratio) {
		// TODO Auto-generated method stub
		
	}

	public boolean preloadBatchNodes() {
		// TODO Auto-generated method stub
		return false;
	}

}
