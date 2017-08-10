package week.of.awesome.game.brushes;

import java.util.UUID;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.WellSpec;

public class WellBrush implements Brush {
	
	private int initialX, initialY;

	@Override
	public void beginBrush(Level level, int x, int y) {
		this.initialX = x;
		this.initialY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y) {
		int minX = Math.min(initialX, x);
		int minY = Math.min(initialY, y);
		
		int maxX = Math.max(initialX, x) + 1;
		int maxY = Math.max(initialY, y) + 1;
		
		WellSpec wellSpec = new WellSpec();
		wellSpec.id = UUID.randomUUID().toString(); // used for wiring purposes
		wellSpec.min = new Vector2(minX, minY);
		wellSpec.max = new Vector2(maxX, maxY);
		
		level.wells.add(wellSpec);
	}

}
