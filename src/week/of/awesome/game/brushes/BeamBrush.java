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
		int maxX = Math.max(initialX, x) + 1;
		
		BeamSpec spec = new BeamSpec();
		spec.min = new Vector2(minX, initialY);
		spec.max = new Vector2(maxX, initialY + 1);
		spec.dir = x > initialX ? BeamSpec.Direction.RIGHT : BeamSpec.Direction.LEFT;
		
		level.beams.add(spec);
		level.undoHistory.add(spec);
	}

}
