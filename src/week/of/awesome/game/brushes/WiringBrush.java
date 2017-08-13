package week.of.awesome.game.brushes;

import week.of.awesome.game.ActivatableSpec;
import week.of.awesome.game.Level;
import week.of.awesome.game.WellSpec;

public class WiringBrush implements Brush {
	
	private int initialX, initialY;

	@Override
	public void beginBrush(Level level, int x, int y, float worldX, float worldY) {
		this.initialX = x;
		this.initialY = y;
	}

	@Override
	public void endBrush(Level level, int x, int y, float worldX, float worldY) {
		ActivatableSpec activatable = level.getActivatable(initialX, initialY);
		if (activatable == null) { return; }
		
		WellSpec well = level.getWell(x, y);
		if (well == null) { return; }
		
		activatable.wellActivatorIDs.add(well.id);
	}

}
