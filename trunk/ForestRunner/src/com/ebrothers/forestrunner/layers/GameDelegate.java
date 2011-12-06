package com.ebrothers.forestrunner.layers;

public interface GameDelegate {

	public void pauseGame();

	public void resumeGame();

	public void loseGame();

	public void winGame();

	public void updateScore();

}
