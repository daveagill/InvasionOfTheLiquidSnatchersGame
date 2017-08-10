package week.of.awesome.framework;

import java.util.Collection;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.IntSet.IntSetIterator;

public class InputService {
	
	// Provides a guess at the max number of keys down/up at once, to seed initial sizes
	private static final int EXPECTED_MAX_KEYCOMBO = 4;
	
	private IntSet keysJustDown = new IntSet(EXPECTED_MAX_KEYCOMBO);
	private IntSet keysJustUp = new IntSet(EXPECTED_MAX_KEYCOMBO);
	private IntSet keysDown = new IntSet(EXPECTED_MAX_KEYCOMBO);
	
	private Collection<KeyWatcher> keyWatchers = new HashSet<>();
	private Collection<MouseWatcher> mouseWatchers = new HashSet<>();
	
	private int screenHeight; // for inverting mouse positions

	public InputService() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		inputMultiplexer.addProcessor(new ExitKeyInputProcessor());
		inputMultiplexer.addProcessor(new InputAdapter() {
			
			@Override
			public boolean keyDown(int keycode) {
				keysJustDown.add(keycode);
				for (KeyWatcher kw : keyWatchers) {
					kw.keyDown(keycode);
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				keysJustUp.add(keycode);
				for (KeyWatcher kw : keyWatchers) {
					kw.keyUp(keycode);
				}
				return false;
			}
			
			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				for (MouseWatcher mw : mouseWatchers) {
					mw.buttonDown(screenX, screenHeight - screenY, button);
				}
				return false;
			}

			@Override
			public boolean touchUp (int screenX, int screenY, int pointer, int button) {
				for (MouseWatcher mw : mouseWatchers) {
					mw.buttonUp(screenX, screenHeight - screenY, button);
				}
				return false;
			}

			@Override
			public boolean touchDragged (int screenX, int screenY, int pointer) {
				for (MouseWatcher mw : mouseWatchers) {
					mw.dragged(screenX, screenHeight - screenY);
					mw.movedOrDragged(screenX, screenHeight - screenY);
				}
				return false;
			}

			@Override
			public boolean mouseMoved (int screenX, int screenY) {
				for (MouseWatcher mw : mouseWatchers) {
					mw.moved(screenX, screenHeight - screenY);
					mw.movedOrDragged(screenX, screenHeight - screenY);
				}
				return false;
			}
			
			@Override
			public boolean scrolled (int amount) {
				for (MouseWatcher mw : mouseWatchers) {
					mw.scrolled(amount);
				}
				return false;
			}
		});
	}
	
	public void updatePostFrame() {
		keysDown.addAll(keysJustDown);
		IntSetIterator it = keysJustUp.iterator();
		while (it.hasNext) {
			keysDown.remove(it.next());
		}
		keysJustDown.clear();
		keysJustUp.clear();
		
		screenHeight = Gdx.graphics.getHeight();
	}
	
	public boolean isAnyPressed() {
		return keysJustDown.size > 0;
	}
	
	public boolean isDown(int key) {
		return keysDown.contains(key);
	}
	
	public boolean isJustDown(int key) {
		return keysJustDown.contains(key);
	}
	
	public boolean isJustUp(int key) {
		return keysJustUp.contains(key);
	}
	
	public void addKeyWatcher(KeyWatcher keyWatcher) {
		keyWatchers.add(keyWatcher);
	}
	
	public void removeKeyWatcher(KeyWatcher keyWatcher) {
		keyWatchers.remove(keyWatcher);
	}
	
	public void addMouseWatcher(MouseWatcher mouseWatcher) {
		mouseWatchers.add(mouseWatcher);
	}
	
	public void removeMouseWatcher(MouseWatcher mouseWatcher) {
		mouseWatchers.remove(mouseWatcher);
	}
	
	public void removeAllWatchers() {
		keyWatchers.clear();
		mouseWatchers.clear();
	}
}
