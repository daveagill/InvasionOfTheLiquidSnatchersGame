package week.of.awesome.game.brushes;

import week.of.awesome.game.Level;

public class ShiftBrush implements Brush {
	
	private int initialX, initialY;

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) {
		this.initialX = x-1;
		this.initialY = y-1;
	}

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		int dx = x-1 - initialX;
		int dy = y-1 - initialY;
		level.shiftX(initialX, dx);
		level.shiftY(initialY, dy);
	}

}
