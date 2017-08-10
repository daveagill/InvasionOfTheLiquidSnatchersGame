package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;

public class RightSlopeBrush implements Brush {

	@Override
	public void beginBrush(Level level, int x, int y) { }

	@Override
	public void endBrush(Level level, int x, int y) {
		level.rightSlopes.add( new Vector2(x, y) );
	}
	
}