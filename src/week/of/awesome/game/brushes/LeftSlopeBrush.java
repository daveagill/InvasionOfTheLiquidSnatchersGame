package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;

public class LeftSlopeBrush implements Brush {

	@Override
	public void beginBrush(Level level, int x, int y) { }

	@Override
	public void endBrush(Level level, int x, int y) {
		level.leftSlopes.add( new Vector2(x, y) );
	}
	
}
