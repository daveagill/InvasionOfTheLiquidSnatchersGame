package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.BeamSpec;
import week.of.awesome.game.Level;

public class BeamBrush implements Brush {
	
	private int initialX, initialY;

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) {
		this.initialX = x;
		this.initialY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		int minX = Math.min(initialX, x);
		int minY = Math.min(initialY, y);
		
		int maxX = Math.max(initialX, x) + 1;
		int maxY = Math.max(initialY, y) + 1;
		
		BeamSpec spec = new BeamSpec();
		spec.min = new Vector2(minX, minY);
		spec.max = new Vector2(maxX, maxY);
		spec.dir = BeamSpec.Direction.RIGHT;
		
		level.beams.add(spec);
		level.undoHistory.add(spec);
	}

}
