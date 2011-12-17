package com.ebrothers.forestrunner.scenes;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.common.Logger;
import com.ebrothers.forestrunner.layers.AlertDialog;
import com.ebrothers.forestrunner.layers.AlertDialog.Builder;
import com.ebrothers.forestrunner.layers.MainGameBackgroundLayer;
import com.ebrothers.forestrunner.layers.MainGameMenuLayer;

public class MainScene extends CCScene {
	private static final String TAG = "MainScene";

	public static MainScene scene() {
		return new MainScene();
	}

	AlertDialog confirmDialog;

	public MainScene() {
		super();
		
		addChild(new MainGameBackgroundLayer());
		addChild(new MainGameMenuLayer());

		Builder builder = new Builder(this);
		builder.setBackground("alert_dialog_bg.png");
		builder.getBackground().setScale(Game.scale_ratio);
		builder.setMessage("Are you sure you want to exit?", "font1.fnt");
		builder.setNegativeButton("button_no01.png", "button_no02.png", this,
				"onNo");
		builder.setPositiveButton("button_yes01.png", "button_yes02.png", this,
				"onYes");
		confirmDialog = builder.create();
		
	}

	public void onYes(Object obj) {
		confirmDialog.dismiss();
		CCDirector.sharedDirector().getActivity().finish();
	}

	public void onNo(Object obj) {
		confirmDialog.dismiss();
	}

	public void back() {
		Logger.d(TAG, "back.");
		if (confirmDialog != null && confirmDialog.isShown()) {
			confirmDialog.dismiss();
		} else {
			confirmDialog.show(1);
		}
	}
}
