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
	
	public void removeAllKeyWatchers() {
		keyWatchers.clear();
	}
}
