package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.ccColor3B;

import com.ebrothers.forestrunner.common.Logger;

public class HighScoreLayer extends BasicLayer {
	String stageArray[] = {"1","2","3","4","5","6","7","8"};
	String highScoreArray[] = {"900","800","700","600","500","400","300","200"};

	public HighScoreLayer() {
		super();

		CCSprite sprite02 = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);
		
		CCBitmapFontAtlas stageName = getFontLabel("Stage", width * 10 / 16,height * 12 / 13, 36);
		addChild(stageName, 2);
		for(int i=0;i<stageArray.length;i++){
			CCBitmapFontAtlas tmpLable = getFontLabel(stageArray[i], width * 10 / 16, height * (11-i) / 13, 30);
			addChild(tmpLable, 2);
		}
		CCBitmapFontAtlas stageTotal = getFontLabel("Total", width * 10 / 16,height * (12-stageArray.length-1) / 13, 36);
		addChild(stageTotal, 2);

		CCBitmapFontAtlas highScoreName = getFontLabel("High Score", width * 13 / 16,
				height * 12 / 13, 36);
		addChild(highScoreName, 2);
		for(int j=0;j<highScoreArray.length;j++){
			CCBitmapFontAtlas tmpLable = getFontLabel(highScoreArray[j], width * 13 / 16, height * (11-j) / 13, 30);
			addChild(tmpLable, 2);
		}
		CCBitmapFontAtlas highScoreAll = getFontLabel(getScoreTotle(), width * 13 / 16,
				height * (12-highScoreArray.length-1) / 13, 36);
		addChild(highScoreAll, 2);
	}

	
	private CCBitmapFontAtlas getFontLabel(String content,float x,float y,int fontSize){
		CCBitmapFontAtlas score = CCBitmapFontAtlas.bitmapFontAtlas(content, "font1.fnt");
		score.setPosition(x, y);
		score.setColor(ccColor3B.ccRED);
		return score;
	}
	
	private String getScoreTotle(){
		int tmp = 0;
		for(int j=0;j<highScoreArray.length;j++){
			tmp += Integer.valueOf(highScoreArray[j]);
		}
		return String.valueOf(tmp);
	}

	
	@Override
	public void onEnter() {
		super.onEnter();
		Logger.e("game", "highScore-----enter");
	}
	
	@Override
	public void onExit() {
		super.onExit();
		Logger.e("game", "highScore-----exit");
	}
}
