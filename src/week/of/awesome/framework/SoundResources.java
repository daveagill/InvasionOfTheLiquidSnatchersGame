package week.of.awesome.framework;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

public class SoundResources implements Disposable {
	private Map<String, Sound> cache = new HashMap<>();
	
	public Sound newSound(String file) {
		Sound s = cache.get(file);
		if (s == null) {
			FileHandle soundFile = Gdx.files.internal(file);
			s = Gdx.audio.newSound(soundFile);
			cache.put(file, s);
		}
		return s;
	}

	@Override
	public void dispose() {
		cache.values().forEach(Disposable::dispose);
	}
}
