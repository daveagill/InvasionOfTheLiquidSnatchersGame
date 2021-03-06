package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.SpaceshipSpec;

public class SpaceshipBrush implements Brush {

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) { }

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		SpaceshipSpec spec = new SpaceshipSpec();
		spec.position = new Vector2(x, y);
		level.spaceships.add(spec);
		level.undoHistory.add(spec);
	}

}