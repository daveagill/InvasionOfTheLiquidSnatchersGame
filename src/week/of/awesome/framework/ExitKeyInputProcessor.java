package week.of.awesome.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class ExitKeyInputProcessor extends InputAdapter {
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ESCAPE) {
			Gdx.app.exit();
		}
		return false;
	}
}
