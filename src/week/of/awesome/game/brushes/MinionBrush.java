package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.MinionSpec;

public class MinionBrush implements Brush {

	@Override
	public void beginBrush(Level level, int x, int y) { }

	@Override
	public void endBrush(Level level, int x, int y) {
		MinionSpec spec = new MinionSpec();
		spec.position = new Vector2(x, y);
		spec.fluidRemaining = 100;
		level.minions.add(spec);
	}

}