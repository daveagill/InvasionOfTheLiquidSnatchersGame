package week.of.awesome.game.brushes;

import com.badlogic.gdx.math.Vector2;

import week.of.awesome.game.Level;
import week.of.awesome.game.SolidSpec;

public class SolidBrush implements Brush {
	
	private int initialX, initialY;

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) {
		this.initialX = x;
		this.initialY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		SolidSpec solid = new SolidSpec();
		solid.min = new Vector2( Math.min(x, initialX), Math.min(y, initialY));
		solid.max = new Vector2( Math.max(x, initialX) + 1, Math.max(y, initialY) + 1);
		
		level.solids.add(solid);
		level.undoHistory.add(solid);
	}


}
