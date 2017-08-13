package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.TrapDoorSpec;

public class TrapDoorBrush implements Brush {
	
	private int initialX;
	
	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) {
		initialX = x;
	}

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		TrapDoorSpec spec = new TrapDoorSpec();
		spec.position = new Vector2(initialX, y);
		spec.width = Math.abs(x - initialX);
		level.trapdoors.add(spec);
		level.undoHistory.add(spec);
	}

}
