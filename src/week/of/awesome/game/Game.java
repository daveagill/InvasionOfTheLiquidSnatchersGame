package week.of.awesome.game;

import com.badlogic.gdx.ApplicationListener;

import week.of.awesome.framework.StandardGameApp;

public class Game {

	public static ApplicationListener create() {
		EmptyGameState initialGameState = new EmptyGameState();
		return new StandardGameApp(initialGameState);
	}

}
