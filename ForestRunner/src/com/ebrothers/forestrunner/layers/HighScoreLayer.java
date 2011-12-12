package com.ebrothers.forestrunner.layers;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.ccColor3B;

import com.ebrothers.forestrunner.common.Levels;
import com.ebrothers.forestrunner.manager.LocalDataManager;

public class HighScoreLayer extends BasicLayer {
	public HighScoreLayer() {
		super();

		CCSprite sprite02 = getNode("game_choose.jpg", 0, 0, 0, 0);
		addChild(sprite02, 1);

		CCBitmapFontAtlas stageName = getFontLabel("Stage", winW * 10 / 16,
				winH * 12 / 13, 36);
		addChild(stageName, 2);

		int levelCount = Levels.count;
		for (int i = 0; i < levelCount; i++) {
			CCBitmapFontAtlas tmpLable = getFontLabel(String.valueOf(i + 1),
					winW * 10 / 16, winH * (11 - i) / 13, 30);
			addChild(tmpLable, 2);
		}

		CCBitmapFontAtlas stageTotal = getFontLabel("Total", winW * 10 / 16,
				winH * (12 - levelCount - 1) / 13, 36);
		addChild(stageTotal, 2);

		CCBitmapFontAtlas highScoreName = getFontLabel("High Score",
				winW * 13 / 16, winH * 12 / 13, 36);
		addChild(highScoreName, 2);

		long totalScore = 0;
		for (int j = 0; j < levelCount; j++) {
			long levelScore = getLevelScore(j);
			CCBitmapFontAtlas tmpLable = getFontLabel(
					String.valueOf(levelScore), winW * 13 / 16, winH * (11 - j)
							/ 13, 30);
			addChild(tmpLable, 2);
			totalScore += levelScore;
		}

		CCBitmapFontAtlas highScoreAll = getFontLabel(
				String.valueOf(totalScore), winW * 13 / 16, winH
						* (12 - levelCount - 1) / 13, 36);
		addChild(highScoreAll, 2);
	}

	/**
	 * @param level
	 *            based 0
	 */
	private long getLevelScore(int level) {
		return (Long) LocalDataManager.getInstance().readSetting(
				String.valueOf(level), 0L);
	}

	private CCBitmapFontAtlas getFontLabel(String content, float x, float y,
			int fontSize) {
		CCBitmapFontAtlas score = CCBitmapFontAtlas.bitmapFontAtlas(content,
				"font1.fnt");
		score.setPosition(x, y);
		score.setColor(ccColor3B.ccc3(156, 97, 0));
		return score;
	}
}
