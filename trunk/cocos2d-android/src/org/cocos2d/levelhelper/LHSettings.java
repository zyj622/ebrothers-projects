package org.cocos2d.levelhelper;

import java.io.File;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class LHSettings {

	private static LHSettings _instance;

	public static LHSettings sharedInstance() {
		if (_instance == null) {
			_instance = new LHSettings();
		}
		return _instance;
	}

	@Deprecated
	// no use
	private boolean m_useRetinaOnIpad;
	private float m_lhPtmRatio;
	private float m_customAlpha; // used by SceneTester
	private int m_newBodyId;
	private CGPoint m_convertRatio;
	private CGPoint m_realConvertRatio;
	private boolean m_convertLevel;
	private boolean m_stretchArt;
	private CGPoint m_possitionOffset;
	private boolean m_levelPaused;
	private String m_imagesFolder;
	private boolean m_isCoronaUser;
	private boolean m_preloadBatchNodes;

	public LHSettings() {
		m_useRetinaOnIpad = true;
		m_convertLevel = true;
		m_lhPtmRatio = 32.0f;
		m_customAlpha = 1.0f;
		m_convertRatio = CGPoint.make(1, 1);
		m_realConvertRatio = CGPoint.make(1, 1);
		m_newBodyId = 0;
		m_stretchArt = true;
		m_possitionOffset = CGPoint.make(0.0f, 0.0f);
		m_levelPaused = false;
		m_imagesFolder = "";
		m_isCoronaUser = false;
		m_preloadBatchNodes = false;
	}

	public void setLhPtmRatio(float ratio) {
		m_lhPtmRatio = ratio;
	}

	public float lhPtmRatio() {
		return m_lhPtmRatio;
	}

	@Deprecated
	public boolean useRetinaOnIpad() {
		return m_useRetinaOnIpad;
	}

	@Deprecated
	public void setUseRetinaOnIpad(boolean r) {
		m_useRetinaOnIpad = r;
	}

	public float customAlpha() {
		return m_customAlpha;
	}

	public void setCustomAlpha(float a) {
		m_customAlpha = a;
	}

	public boolean convertLevel() {
		return m_convertLevel;
	}

	public void setConvertLevel(boolean c) {
		m_convertLevel = c;
	}

	public boolean levelPaused() {
		return m_levelPaused;
	}

	public void setLevelPaused(boolean p) {
		m_levelPaused = p;
	}

	public boolean isCoronaUser() {
		return m_isCoronaUser;
	}

	public void setIsCoronaUser(boolean u) {
		m_isCoronaUser = u;
	}

	public boolean preloadBatchNodes() {
		return m_preloadBatchNodes;
	}

	public void setPreloadBatchNodes(boolean p) {
		m_preloadBatchNodes = p;
	}

	public int newBodyId() {
		return m_newBodyId++;
	};

	public void setImageFolder(String img) {
		if (img != null && img.length() != 0) {
			m_imagesFolder = img;
		}
	};

	public String imageFolder() {
		return m_imagesFolder;
	};

	public String imagePath(String image) {
		if (isIpad()) {
			String name = image.substring(0, image.lastIndexOf('.'));
			String ext = image.substring(image.lastIndexOf('.'));
			String hdImage = name + "-hd" + ext;
			if (new File(hdImage).exists()) {
				return hdImage;
			}
			return image;
		}

		return image;
	};

	public boolean shouldScaleImageOnRetina(String image) {
		return image.contains("-hd");
	};

	@Deprecated
	public boolean isIpad() {
		CGSize wSize = CCDirector.sharedDirector().winSize();
		if ((wSize.width == 1024 || wSize.width == 768)
				&& (wSize.height == 1024 || wSize.height == 768)) {
			return true;
		}
		return false;
	};

	public void setStretchArt(boolean value) {
		m_stretchArt = value;
		m_possitionOffset.x = 0.0f;
		m_possitionOffset.y = 0.0f;
	};

	public CGPoint possitionOffset() {
		return m_possitionOffset;
	};

	public void setConvertRatio(CGPoint val) {
		m_convertRatio = val;
		m_realConvertRatio = val;
		if (!m_stretchArt) {
			if (isIpad()) {
				if (m_convertRatio.x > 1.0 || m_convertRatio.y > 1.0f) {
					m_convertRatio.x = 2.0f;
					m_convertRatio.y = 2.0f;

					if (CCDirector.sharedDirector().winSize().width == 1024.0f) {
						m_possitionOffset.x = 32.0f;
						m_possitionOffset.y = 64.0f;
					} else {
						m_possitionOffset.x = 64.0f;
						m_possitionOffset.y = 32.0f;
					}
				}
			}
		}
	};

	public CGPoint convertRatio() {
		if (!m_convertLevel)
			return CGPoint.make(1, 1);
		return m_convertRatio;
	};

	public CGPoint realConvertRatio() {
		if (!m_convertLevel)
			return CGPoint.make(1, 1);
		return m_realConvertRatio;
	};

}
