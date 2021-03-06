package week.of.awesome.game.brushes;

import week.of.awesome.game.Level;

public interface Brush {
	public void beginBrush(Level level, int x, int y, float worldX, float worldY);
	public void endBrush(Level level, int x, int y, float worldX, float worldY);
	public default String getName() { return this.getClass().getSimpleName(); }
}
