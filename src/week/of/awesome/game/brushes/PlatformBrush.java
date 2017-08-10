package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.PlatformSpec;

public class PlatformBrush implements Brush {
	
	private int platformX, platformY;

	@Override
	public void beginBrush(Level level, int x, int y) {
		this.platformX = x;
		this.platformY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y) {
		if (y < platformY) { return; }
		
		PlatformSpec platformSpec = new PlatformSpec();
		platformSpec.position = new Vector2(platformX, platformY);
		platformSpec.height = y - platformY + 1;
		
		level.platforms.add(platformSpec);
	}

}
