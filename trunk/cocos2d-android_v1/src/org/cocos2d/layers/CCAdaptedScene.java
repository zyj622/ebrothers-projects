package org.cocos2d.layers;

import org.cocos2d.nodes.CCDirector;

/**
 * Adapted scene with screen size, based 480*320.
 * 
 * @author fengch
 * @since 2012-1-16
 */
public class CCAdaptedScene extends CCScene {
	public static final int MODE_ADAPTE_BY_HV = 0;
	public static final int MODE_ADAPTE_BY_H = 1;
	public static final int MODE_ADAPTE_BY_V = 2;

	public static CCAdaptedScene node() {
		return new CCAdaptedScene();
	}

	public static void decorate(CCScene scene) {
		decorate(scene, MODE_ADAPTE_BY_HV);
	}

	public static void decorate(CCScene scene, int mode) {
		scene.setPosition(0, 0);
		scene.setAnchorPoint(0, 0);
		adapteByMode(scene, mode);
	}

	public CCAdaptedScene() {
		super();
		setPosition(0, 0);
		setAnchorPoint(0, 0);
		setAdapteMode(MODE_ADAPTE_BY_HV);
	}

	protected void setAdapteMode(int mode) {
		adapteByMode(this, mode);
	}

	private static void adapteByMode(CCScene scene, int mode) {
		switch (mode) {
		case MODE_ADAPTE_BY_HV:
			CCScreenAdapter.sharedSceneAdapter().adapte(scene);
			break;
		case MODE_ADAPTE_BY_H:
			CCScreenAdapter.sharedSceneAdapter().adapteByHorizontal(scene);
			scene.setPosition(scene.getPosition().x, scene.getPosition().y
					- (scene.getScaleY()
							* CCScreenAdapter.sharedSceneAdapter()
									.getBasedHeight() - CCDirector
							.sharedDirector().winSize().height) / 2f);
			break;
		case MODE_ADAPTE_BY_V:
			CCScreenAdapter.sharedSceneAdapter().adapteByVertical(scene);
			scene.setPosition(
					scene.getPosition().x
							+ (CCDirector.sharedDirector().winSize().width - CCScreenAdapter
									.sharedSceneAdapter().getBasedWidth()
									* scene.getScaleX()) / 2f,
					scene.getPosition().y);
			break;
		default:
			break;
		}
	}
}
