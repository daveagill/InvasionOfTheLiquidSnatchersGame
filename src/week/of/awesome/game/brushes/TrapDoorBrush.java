package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.TrapDoorSpec;

public class TrapDoorBrush implements Brush {
	
	@Override
	public void beginBrush(Level level, int x, int y) { }

	@Override
	public void endBrush(Level level, int x, int y) {
		TrapDoorSpec spec = new TrapDoorSpec();
		spec.position = new Vector2(x, y);
		level.trapdoors.add(spec);
	}

}