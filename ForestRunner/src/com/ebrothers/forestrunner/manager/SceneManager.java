package com.ebrothers.forestrunner.manager;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;

import com.ebrothers.forestrunner.common.Game;
import com.ebrothers.forestrunner.scenes.GameOverScene;
import com.ebrothers.forestrunner.scenes.GameScene;
import com.ebrothers.forestrunner.scenes.HighScoreScene;
import com.ebrothers.forestrunner.scenes.LevelSelectScene;
import com.ebrothers.forestrunner.scenes.MainScene;

/**
 * Using to management scenes: MainMenu Scene, Stage Scene, Game Scene, GameOver
 * Scene, HighScore Scene, every scene shall be remove frame cache and readd
 * frame cache according to itself .plist in assets folder.
 * 
 * And make sure to navigate to next scene using <code>replaceScene</code> of
 * <code>CCDirector</code>.
 * 
 * @author fancy
 */
public class SceneManager {

	public static final int SCENE_MAINMENU = 0;
	public static final int SCENE_STAGES = 1;
	public static final int SCENE_GAME = 2;
	public static final int SCENE_GAMEOVER = 3;
	public static final int SCENE_HIGHSCORE = 4;

	private static SceneManager _instance;

	public static SceneManager sharedSceneManager() {
		if (_instance == null) {
			_instance = new SceneManager();
		}
		return _instance;
	}

	public void replaceTo(int scene) {
		switch (scene) {
		case SCENE_MAINMENU:
			CCDirector.sharedDirector().replaceScene(MainScene.scene());
			break;
		case SCENE_STAGES:
			CCDirector.sharedDirector().replaceScene(LevelSelectScene.scene());
			break;
		case SCENE_GAME:
			CCDirector.sharedDirector().replaceScene(
					GameScene.scene(Game.current_level));
			break;
		case SCENE_GAMEOVER:
			CCDirector.sharedDirector().replaceScene(GameOverScene.scene());
			break;
		case SCENE_HIGHSCORE:
			CCDirector.sharedDirector().replaceScene(HighScoreScene.scene());
			break;
		default:
			break;
		}
	}

	/**
	 * @return true back success, false on top
	 */
	public boolean backTo() {
		CCScene scene = CCDirector.sharedDirector().getRunningScene();
		if (scene instanceof MainScene) {
			// ((MainScene) scene).back();
			return false;
		} else if (scene instanceof LevelSelectScene) {
			replaceTo(SCENE_MAINMENU);
			return true;
		} else if (scene instanceof GameScene) {
			replaceTo(SCENE_STAGES);
			return true;
		} else if (scene instanceof GameOverScene) {
			replaceTo(SCENE_STAGES);
			return true;
		} else if (scene instanceof HighScoreScene) {
			replaceTo(SCENE_MAINMENU);
			return true;
		}
		return false;
	}
}
