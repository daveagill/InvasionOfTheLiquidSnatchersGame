package week.of.awesome.game;

import com.badlogic.gdx.ApplicationListener;

import week.of.awesome.framework.StandardGameApp;

public class Game {

	public static ApplicationListener create() {
		PlayGameState initialGameState = new PlayGameState();
		return new StandardGameApp(initialGameState);
	}

}
