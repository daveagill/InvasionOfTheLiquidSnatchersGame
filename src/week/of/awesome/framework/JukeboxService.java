package week.of.awesome.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class JukeboxService implements Disposable {
	private static final float MAX_VOLUME = 0.3f;
	private static final float FADE_SECS = 1f;
	
	private String currentFile;
	private Music currentlyPlaying;
	private Music previouslyPlaying;
	
	public void play(String file) {
		if (file.equals(currentFile)) { return; }
		Music m = Gdx.audio.newMusic(Gdx.files.internal(file));
		m.setLooping(true);
		m.play();
		
		setPrevious(currentlyPlaying);
		currentlyPlaying = m;
		currentFile = file;
	}
	
	public void update(float dt) {
		if (currentlyPlaying == null) { return; }
		
		// fade new track in (and maintain volume thereafter)
		currentlyPlaying.setVolume( Math.min(MAX_VOLUME, (currentlyPlaying.getVolume() * FADE_SECS + dt) / FADE_SECS) );
		
		// fade previous track out
		if (previouslyPlaying != null) {
			previouslyPlaying.setVolume( Math.max(0, (previouslyPlaying.getVolume() * FADE_SECS - dt) / FADE_SECS) );
			if (previouslyPlaying.getVolume() == 0f) {
				setPrevious(null);
			}
		}
	}
	
	private void setPrevious(Music m) {
		if (previouslyPlaying != null) {
			previouslyPlaying.dispose();
		}
		previouslyPlaying = m;
	}

	@Override
	public void dispose() {
		if (currentlyPlaying != null) {
			currentlyPlaying.dispose();
		}
		
		if (previouslyPlaying != null) {
			previouslyPlaying.dispose();
		}
	}
}
