package com.ebrothers.forestrunner.layers;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;

import android.content.Intent;
import android.net.Uri;

import com.ebrothers.forestrunner.common.Globals;
import com.ebrothers.forestrunner.manager.SoundManager;

public class BasicLayer extends CCLayer {
	protected CCSpriteFrameCache cache;
	
	protected float width;
	protected float height;
	
	public BasicLayer(){
		super();
		width = CCDirector.sharedDirector().winSize().width;
		height = CCDirector.sharedDirector().winSize().height;
		cache = CCSpriteFrameCache.sharedSpriteFrameCache();
		
	}
	
	
	protected CCSprite getNode(String name, float x, float y,float x_anchor,float y_anchor) {
		CCSpriteFrame frame = cache.getSpriteFrame(name);
		CCSprite sprite = CCSprite.sprite(frame);
		sprite.setAnchorPoint(x_anchor, y_anchor);
		sprite.setScaleX(Globals.scale_ratio_x);
		sprite.setScaleY(Globals.scale_ratio_y);
		sprite.setPosition(x, y);
		return sprite;
	}
	
	
	protected CCSprite getNode(String name, float x, float y) {
		CCSpriteFrame frame = cache.getSpriteFrame(name);
		CCSprite sprite = CCSprite.sprite(frame);
		sprite.setPosition(x, y);
		return sprite;
	}
	
	protected void moreGame(){
		SoundManager.getInstance().playEffect(CCDirector.sharedDirector().getActivity(), SoundManager.MUSIC_BUTTON);
		Intent it = new Intent(Intent.ACTION_VIEW,
				Uri.parse(Globals.more_url));
		if (CCDirector.sharedDirector().getActivity() != null)
			CCDirector.sharedDirector().getActivity().startActivity(it);
	}

}
